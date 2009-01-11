/**
 * The (S)ubversion Re(po)sitory (S)earch (E)ngine (SupoSE for short).
 *
 * Copyright (c) 2007, 2008, 2009 by SoftwareEntwicklung Beratung Schulung (SoEBeS)
 * Copyright (c) 2007, 2008, 2009 by Karl Heinz Marbaise
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301 USA
 *
 * The License can viewed online under http://www.gnu.org/licenses/gpl.html
 * If you have any questions about the Software or about the license
 * just write an email to license@soebes.de
 */
package com.soebes.supose.parse.java;

import static org.testng.Assert.assertTrue;

import java.util.HashMap;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.CommonTokenStream;
import org.testng.annotations.Test;

import com.soebes.supose.TestBase;

@Test
public class JavaParserTest extends TestBase {

	public void  testFirstJavaFile() throws Exception {
		//We won't name the Java test files ".java", cause the compiler would
		//compile them, so we have no access to the real source file.
		ANTLRFileStream input = new ANTLRFileStream(getFileResource("Test1.java.test"));
		JavaLexer lexer = new JavaLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		JavaParser parser = new JavaParser(tokens);
		parser.compilationUnit();

		HashMap methods = parser.getMethods();
		assertTrue(methods.size() == 5, "We had expected to get five different methods");
//		for (Iterator iter = methods.keySet().iterator(); iter.hasNext(); ) {
//			String key = (String) iter.next();
//			String value = (String) methods.get(key);
//			System.out.println("Key:" + key + " value:" + value);
//		}
		
		assertTrue(lexer.getComments().size() == 4, "We have expected to get four different comments");
//		for (String item : lexer.getComments()) {
//			System.out.println("Comment: " + item);
//		}
	}

}
