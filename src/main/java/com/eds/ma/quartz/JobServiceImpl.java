package com.eds.ma.quartz;

import com.eds.ma.config.SysConfig;
import com.eds.ma.quartz.service.IJobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JobServiceImpl implements IJobService {
	
	protected Logger log = LoggerFactory.getLogger(JobServiceImpl.class);


	@Autowired
	private SysConfig sysConfig;

}
