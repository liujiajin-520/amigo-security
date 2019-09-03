/*
 *      Copyright (c) 2018-2028, Chill Zhuang All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the dreamlu.net developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: Chill 庄骞 (smallchill@163.com)
 */
package org.springblade.common.event;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.launch.props.BladeProperties;
import org.springblade.core.launch.server.ServerInfo;
import org.springblade.core.log.constant.EventConstant;
import org.springblade.core.log.event.UsualLogEvent;
import org.springblade.core.log.model.LogUsual;
import org.springblade.core.secure.utils.SecureUtil;
import org.springblade.core.tool.utils.DateUtil;
import org.springblade.core.tool.utils.UrlUtil;
import org.springblade.core.tool.utils.WebUtil;
import org.springblade.modules.system.service.ILogService;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

/**
 * 异步监听日志事件
 *
 * @author Chill
 */
@Slf4j
@AllArgsConstructor
public class UsualLogListener {

	private final ILogService logService;
	private final ServerInfo serverInfo;
	private final BladeProperties bladeProperties;

	@Async
	@Order
	@EventListener(UsualLogEvent.class)
	public void saveUsualLog(UsualLogEvent event) {
		Map<String, Object> source = (Map<String, Object>) event.getSource();
		LogUsual logUsual = (LogUsual) source.get(EventConstant.EVENT_LOG);
		HttpServletRequest request = (HttpServletRequest) source.get(EventConstant.EVENT_REQUEST);
		logUsual.setRequestUri(UrlUtil.getPath(request.getRequestURI()));
		logUsual.setUserAgent(request.getHeader(WebUtil.USER_AGENT_HEADER));
		logUsual.setMethod(request.getMethod());
		logUsual.setParams(WebUtil.getRequestParamString(request));
		logUsual.setServerHost(serverInfo.getHostName());
		logUsual.setServiceId(bladeProperties.getName());
		logUsual.setEnv(bladeProperties.getEnv());
		logUsual.setServerIp(serverInfo.getIpWithPort());
		logUsual.setCreateBy(SecureUtil.getUserAccount(request));
		logUsual.setCreateTime(DateUtil.now());
		logService.saveUsualLog(logUsual);
	}

}
