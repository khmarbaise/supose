package com.soebes.supose.scan;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Map;

import org.apache.log4j.Logger;
import org.testng.annotations.Test;
import org.tmatesoft.svn.core.ISVNCanceller;
import org.tmatesoft.svn.core.SVNCancelException;
import org.tmatesoft.svn.core.SVNErrorCode;
import org.tmatesoft.svn.core.SVNErrorMessage;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNPropertyValue;
import org.tmatesoft.svn.core.internal.io.fs.FSCommitter;
import org.tmatesoft.svn.core.internal.io.fs.FSDeltaConsumer;
import org.tmatesoft.svn.core.internal.io.fs.FSFS;
import org.tmatesoft.svn.core.internal.io.fs.FSTransactionInfo;
import org.tmatesoft.svn.core.internal.io.fs.FSTransactionRoot;
import org.tmatesoft.svn.core.internal.util.SVNPathUtil;
import org.tmatesoft.svn.core.internal.wc.ISVNLoadHandler;
import org.tmatesoft.svn.core.internal.wc.SVNAdminHelper;
import org.tmatesoft.svn.core.internal.wc.SVNDumpStreamParser;
import org.tmatesoft.svn.core.internal.wc.SVNErrorManager;
import org.tmatesoft.svn.core.wc.ISVNEventHandler;
import org.tmatesoft.svn.core.wc.admin.ISVNAdminEventHandler;
import org.tmatesoft.svn.core.wc.admin.SVNAdminEvent;
import org.tmatesoft.svn.core.wc.admin.SVNAdminEventAction;
import org.tmatesoft.svn.util.SVNLogType;

import com.soebes.supose.TestBase;

public class DumpTest extends TestBase implements ISVNCanceller, ISVNLoadHandler {
	private static final boolean weWantToStop = false;
	private static Logger LOGGER = Logger.getLogger(DumpTest.class);

    private String myParentDir;
    private NodeBaton myCurrentNodeBaton;
    private FSFS myFSFS;
    private ISVNAdminEventHandler myProgressHandler;
    private RevisionBaton myCurrentRevisionBaton;
	
    @Test
	public void testDump() throws SVNException, IOException {
		String dumpFile = getMavenBaseDir() 
		+ File.separatorChar + "src" 
		+ File.separatorChar + "test" 
		+ File.separatorChar + "resources"
		+ File.separatorChar + "repos.dump";
		
        CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();

        SVNDumpStreamParser parser = new SVNDumpStreamParser(this);;
        FileInputStream fis = new FileInputStream(dumpFile);
        parser.parseDumpStream(fis, this, decoder);
        fis.close();
	}

	public void checkCancelled() throws SVNCancelException {
		if (weWantToStop) {
			LOGGER.warn("Cancellation activated.");
			throw new SVNCancelException();
		}
	}

	public void applyTextDelta() throws SVNException {
		LOGGER.info("applyTextDelta");
	}

	public void closeNode() throws SVNException {
		LOGGER.info("closeNode");
	}

	public void closeRevision() throws SVNException {
		LOGGER.info("closeRevision");
        if (myCurrentRevisionBaton != null) {
        	
        }
	}

	public void deleteNodeProperty(String propertyName) throws SVNException {
		LOGGER.info("deleteNodeProperty");
	}

	public void openNode(Map headers) throws SVNException {
		LOGGER.info("openNode");
        if (myCurrentRevisionBaton.myRevision == 0) {
            SVNErrorMessage err = SVNErrorMessage.create(SVNErrorCode.STREAM_MALFORMED_DATA, "Malformed dumpstream: Revision 0 must not contain node records");
            SVNErrorManager.error(err, SVNLogType.FSFS);
        }
        
        myCurrentNodeBaton = createNodeBaton(headers);
        String message = null;
        switch (myCurrentNodeBaton.myAction) {
            case SVNAdminHelper.NODE_ACTION_CHANGE:
                message = "     * editing path : " + myCurrentNodeBaton.myPath + " ...";
                if (myProgressHandler != null) {
                    SVNAdminEvent event = new SVNAdminEvent(SVNAdminEventAction.REVISION_LOAD_EDIT_PATH, myCurrentNodeBaton.myPath, message); 
                    myProgressHandler.handleAdminEvent(event, ISVNEventHandler.UNKNOWN);
                }
                break;
            case SVNAdminHelper.NODE_ACTION_DELETE:
                message = "     * deleting path : " + myCurrentNodeBaton.myPath + " ...";
                if (myProgressHandler != null) {
                    SVNAdminEvent event = new SVNAdminEvent(SVNAdminEventAction.REVISION_LOAD_DELETE_PATH, myCurrentNodeBaton.myPath, message); 
                    myProgressHandler.handleAdminEvent(event, ISVNEventHandler.UNKNOWN);
                }
//                myCurrentRevisionBaton.getCommitter().deleteNode(myCurrentNodeBaton.myPath);
                break;
            case SVNAdminHelper.NODE_ACTION_ADD:
                message = "     * adding path : " + myCurrentNodeBaton.myPath + " ...";
                if (maybeAddWithHistory(myCurrentNodeBaton)) {
                    message += "COPIED...";
                }
                if (myProgressHandler != null) {
                    SVNAdminEvent event = new SVNAdminEvent(SVNAdminEventAction.REVISION_LOAD_ADD_PATH, myCurrentNodeBaton.myPath, message); 
                    myProgressHandler.handleAdminEvent(event, ISVNEventHandler.UNKNOWN);
                }
                break;
            case SVNAdminHelper.NODE_ACTION_REPLACE:
                message = "     * replacing path : " + myCurrentNodeBaton.myPath + " ...";
//                myCurrentRevisionBaton.getCommitter().deleteNode(myCurrentNodeBaton.myPath);
                if (maybeAddWithHistory(myCurrentNodeBaton)) {
                    message += "COPIED...";
                }
                if (myProgressHandler != null) {
                    SVNAdminEvent event = new SVNAdminEvent(SVNAdminEventAction.REVISION_LOAD_REPLACE_PATH, myCurrentNodeBaton.myPath, message); 
                    myProgressHandler.handleAdminEvent(event, ISVNEventHandler.UNKNOWN);
                }
                break;
            default:
                SVNErrorMessage err = SVNErrorMessage.create(SVNErrorCode.STREAM_UNRECOGNIZED_DATA, "Unrecognized node-action on node ''{0}''", myCurrentNodeBaton.myPath);
                SVNErrorManager.error(err, SVNLogType.FSFS);
        }
        LOGGER.info(message);

	}

	public void openRevision(Map headers) throws SVNException {
		LOGGER.info("openRevision");
        long revision = -1;
        myCurrentRevisionBaton = new RevisionBaton();
        if (headers.containsKey(SVNAdminHelper.DUMPFILE_REVISION_NUMBER)) {
            try {
                revision = Long.parseLong((String) headers.get(SVNAdminHelper.DUMPFILE_REVISION_NUMBER));
                LOGGER.info("Revision:" + revision);
                myCurrentRevisionBaton.myRevision = revision;
            } catch (NumberFormatException nfe) {
                SVNErrorMessage err = SVNErrorMessage.create(SVNErrorCode.STREAM_MALFORMED_DATA, "Cannot parse revision ({0}) in dump file", headers.get(SVNAdminHelper.DUMPFILE_REVISION_NUMBER));
                SVNErrorManager.error(err, SVNLogType.FSFS);
            }
        }
	}

	public void parseTextBlock(InputStream dumpStream, long contentLength, boolean isDelta) throws SVNException {
		LOGGER.info("parseTextBlock");
		try {
			//Just skip over the current data...
			dumpStream.skip(contentLength);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void parseUUID(String uuid) throws SVNException {
		LOGGER.info("parseUUID");
	}

	public void removeNodeProperties() throws SVNException {
		LOGGER.info("removeNodeProperties");
	}

	public void setFullText() throws SVNException {
		LOGGER.info("setFullText");
	}

	public void setNodeProperty(String propertyName,
			SVNPropertyValue propertyValue) throws SVNException {
		LOGGER.info("setNodeProperty");
	}

	public void setRevisionProperty(String propertyName, SVNPropertyValue propertyValue) throws SVNException {
		LOGGER.info("setRevisionProperty (" + propertyName + " -> " + propertyValue);
	}

    private boolean maybeAddWithHistory(NodeBaton nodeBaton) throws SVNException {
    	return true;
    }
	
   private NodeBaton createNodeBaton(Map headers) throws SVNException {
        NodeBaton baton = new NodeBaton();
        baton.myKind = SVNNodeKind.UNKNOWN;
        if (headers.containsKey(SVNAdminHelper.DUMPFILE_NODE_PATH)) {
            String nodePath = (String) headers.get(SVNAdminHelper.DUMPFILE_NODE_PATH); 
            if (myParentDir != null) {
                baton.myPath = SVNPathUtil.getAbsolutePath(SVNPathUtil.append(myParentDir, nodePath));
            } else {
                baton.myPath = SVNPathUtil.getAbsolutePath(SVNPathUtil.canonicalizePath(nodePath));
            }
        }
        
        if (headers.containsKey(SVNAdminHelper.DUMPFILE_NODE_KIND)) {
            baton.myKind = SVNNodeKind.parseKind((String) headers.get(SVNAdminHelper.DUMPFILE_NODE_KIND));
        }
        
        baton.myAction = SVNAdminHelper.NODE_ACTION_UNKNOWN;
        if (headers.containsKey(SVNAdminHelper.DUMPFILE_NODE_ACTION)) {
            String action = (String) headers.get(SVNAdminHelper.DUMPFILE_NODE_ACTION);
            if ("change".equals(action)) {
                baton.myAction = SVNAdminHelper.NODE_ACTION_CHANGE;
            } else if ("add".equals(action)) {
                baton.myAction = SVNAdminHelper.NODE_ACTION_ADD;
            } else if ("delete".equals(action)) {
                baton.myAction = SVNAdminHelper.NODE_ACTION_DELETE;
            } else if ("replace".equals(action)) {
                baton.myAction = SVNAdminHelper.NODE_ACTION_REPLACE;
            }
        }
        
        baton.myCopyFromRevision = -1;
        if (headers.containsKey(SVNAdminHelper.DUMPFILE_NODE_COPYFROM_REVISION)) {
            try {
                baton.myCopyFromRevision = Long.parseLong((String) headers.get(SVNAdminHelper.DUMPFILE_NODE_COPYFROM_REVISION)); 
            } catch (NumberFormatException nfe) {
                SVNErrorMessage err = SVNErrorMessage.create(SVNErrorCode.STREAM_MALFORMED_DATA, "Cannot parse revision ({0}) in dump file", headers.get(SVNAdminHelper.DUMPFILE_NODE_COPYFROM_REVISION));
                SVNErrorManager.error(err, SVNLogType.FSFS);
            }
        }
        
        if (headers.containsKey(SVNAdminHelper.DUMPFILE_NODE_COPYFROM_PATH)) {
            String copyFromPath = (String) headers.get(SVNAdminHelper.DUMPFILE_NODE_COPYFROM_PATH);
            if (myParentDir != null) {
                baton.myCopyFromPath = SVNPathUtil.append(myParentDir, copyFromPath);
            } else {
                baton.myCopyFromPath = SVNPathUtil.canonicalizePath(copyFromPath);
            }
            baton.myCopyFromPath = SVNPathUtil.getAbsolutePath(baton.myCopyFromPath);
        }
        
        if (headers.containsKey(SVNAdminHelper.DUMPFILE_TEXT_CONTENT_CHECKSUM)) {
            baton.myResultChecksum = (String) headers.get(SVNAdminHelper.DUMPFILE_TEXT_CONTENT_CHECKSUM);
        }        
        
        if (headers.containsKey(SVNAdminHelper.DUMPFILE_TEXT_DELTA_BASE_CHECKSUM)) {
            baton.myBaseChecksum = (String) headers.get(SVNAdminHelper.DUMPFILE_TEXT_DELTA_BASE_CHECKSUM);
        }
        
        if (headers.containsKey(SVNAdminHelper.DUMPFILE_TEXT_COPY_SOURCE_CHECKSUM)) {
            baton.myCopySourceChecksum = (String) headers.get(SVNAdminHelper.DUMPFILE_TEXT_COPY_SOURCE_CHECKSUM);
        }
        return baton;
    }
	
   private class RevisionBaton {
       FSTransactionInfo myTxn;
       FSTransactionRoot myTxnRoot;
       long myRevision;
       long myRevisionOffset;
       SVNPropertyValue myDatestamp;
       
       private FSCommitter myCommitter;
       private FSDeltaConsumer myDeltaConsumer;
       
       public FSDeltaConsumer getConsumer() {
           if (myDeltaConsumer == null) {
               myDeltaConsumer = new FSDeltaConsumer("", myTxnRoot, myFSFS, getCommitter(), null, null);
           }
           return myDeltaConsumer;
       }
       
       public FSCommitter getCommitter() {
           if (myCommitter == null) {
               myCommitter = new FSCommitter(myFSFS, myTxnRoot, myTxn, null, null);
           }
           return myCommitter;
       }
   }

   private class NodeBaton {
       String myPath;
       SVNNodeKind myKind;
       int myAction;
       String myBaseChecksum;
       String myResultChecksum;
       String myCopySourceChecksum;
       long myCopyFromRevision;
       String myCopyFromPath;
   }
   
}
