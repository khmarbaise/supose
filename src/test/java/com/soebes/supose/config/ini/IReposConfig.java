package com.soebes.supose.config.ini;

public interface IReposConfig {

	String getFromrev();
	void setFromrev(String rev);
	String getTorev();
	void setTorev(String rev);
	String getIndexusername();
	void setIndexusername(String name);
	String getIndexpassword();
	void setIndexpassword();
	String getUrl();
	void setUrl(String url);
	String getResultindex();
	void setResultindex(String index);
	String getCron();
	void setCron(String cron);
}
