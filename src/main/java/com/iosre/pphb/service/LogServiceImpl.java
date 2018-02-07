package com.iosre.pphb.service;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iosre.pphb.dto.UserOpLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class LogServiceImpl {
	private final static Logger logger = LoggerFactory.getLogger(LogServiceImpl.class);
	private final static ObjectMapper jsonMapper = new ObjectMapper();

	@Autowired
    private MongoTemplate mongoTemplate;

	@Async
	public void saveUserOpLog(UserOpLog userOpLog, String collectionName) {
		String rs = userOpLog.getResult();
		
		String allParamsString = null;
		
		//转换params对象为JSON字符串
		if(userOpLog.getParams().size()>0){
			try {
				allParamsString = jsonMapper.writeValueAsString(userOpLog.getParams());
			} catch (JsonGenerationException | JsonMappingException e) {
				//allParamsString = userOpLog.getAllParams().toString();
				logger.warn("转换params为String时发生了异常！{}", userOpLog.getParams());
				
				for(Object o : userOpLog.getParams()){
					if(o instanceof Map<?,?>){
						@SuppressWarnings("unchecked")
						
						Map<String, Object> m = (Map<String, Object>)o;
						logger.warn("bigKey - value:{}, size:{}", o, m.size());
						for(String k : m.keySet()){
							logger.warn("key: {} value: {}",k, m.get(k).toString());
						}
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		
		//转换result对象为JSON字符串		
		String resultString = null;
		try {
			resultString = jsonMapper.writeValueAsString(userOpLog.getResult());
		} catch (JsonGenerationException | JsonMappingException e) {
			resultString = userOpLog.getResult().toString();
			logger.warn("转换result为String时发生了异常！", resultString);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		//记录log到文件中
		//用tab分隔，以方便后期处理。
		logger.info("请求已处理.	Method={},	Cost={}ms,	Ip={}, Controller={},	Params={},	Result={}",
				userOpLog.getMethod(), userOpLog.getMs(), userOpLog.getIp(),userOpLog.getController(),
				allParamsString, resultString);
		
		//是否保存到mongo中
		try {
			if(saveToMongo(userOpLog.getMethod())){
				if(rs!=null){
					userOpLog.setRMsg(rs);
				}
				mongoTemplate.save(userOpLog, collectionName);
				logger.debug("Saved log to mongo.");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

	}
	
	/**
	 * 判断一个方法是否需要记录到mongo中
	 * @param methodName
	 * @return
	 */
	private boolean saveToMongo(String methodName) {
		if("search".equalsIgnoreCase(methodName)){
			return false;
		}
		return true;
	}

}
