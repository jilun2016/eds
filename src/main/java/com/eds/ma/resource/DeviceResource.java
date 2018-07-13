package com.eds.ma.resource;

import com.eds.ma.bis.common.entity.EdsConfig;
import com.eds.ma.bis.common.service.IEdsConfigService;
import com.eds.ma.bis.device.service.IDeviceService;
import com.eds.ma.bis.device.vo.DeviceFaqInfoVo;
import com.eds.ma.bis.device.vo.DeviceInfoVo;
import com.eds.ma.config.SysConfig;
import com.eds.ma.resource.request.*;
import com.eds.ma.rest.integration.annotation.NoAuth;
import com.eds.ma.util.DistanceUtil;
import com.xcrm.common.page.Pagination;
import com.xcrm.log.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 设备资源
 * @Author gaoyan
 * @Date: 2018/3/31
 */
@Path("/device")
public class DeviceResource extends BaseAuthedResource{
	
	private static Logger logger = Logger.getLogger(DeviceResource.class);

	@Autowired
	private IDeviceService deviceService;

	@Autowired
	private IEdsConfigService edsConfigService;

	/**
	 * 查询指定范围的设备
	 * @param request 查询条件
	 */
	@GET
	@Path("/nearby/devices")
	@Produces(MediaType.APPLICATION_JSON)
	@NoAuth
	public DeviceInfoVo queryNearbyDevices(@Valid @BeanParam SearchDeviceRequest request) {
		logger.debug("----DeviceResource.queryNearbyDevices({})----", request);
		if(Objects.isNull(request.getDistance())){
			EdsConfig edsConfig = edsConfigService.queryEdsConfig();
			request.setDistance(edsConfig.getNearbyDistance());
		}
        double[] distanceArr = DistanceUtil.getAround(request.getUserLat().doubleValue(), request.getUserLng().doubleValue(), request.getDistance());
        List<DeviceInfoVo.DeviceDetailVo> deviceDetailVos = deviceService.queryNearbyDevices(distanceArr[0], distanceArr[1], distanceArr[2],distanceArr[3]);
        DeviceInfoVo deviceInfoVo = new DeviceInfoVo();
        deviceInfoVo.setUserLat(request.getUserLat());
        deviceInfoVo.setUserLng(request.getUserLng());
        deviceInfoVo.setDeviceDetailVoList(deviceDetailVos);
        return deviceInfoVo;
	}

	/**
	 * 租借设备
	 * @param request
	 */
	@POST
	@Path("/rent")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deviceRent(@Valid DeviceRentRequest request) {
		logger.debug("WxMaResource.deviceRent({},{})",super.getOpenId(), request);
        deviceService.deviceRent(request.getDeviceId(),super.getUser().getId(),request.getUserLat(),request.getUserLng());
		return Response.status(Response.Status.CREATED).build();
	}

	/**
	 * 归还设备
	 * @param request
	 */
	@POST
	@Path("/return")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deviceReturn(@Valid DeviceReturnRequest request) {
		logger.debug("WxMaResource.deviceReturn({},{},{})",super.getOpenId(),super.getUser(), request);
		Long orderId = deviceService.deviceReturn(request.getDeviceId(),super.getUser(),request.getUserLat(),request.getUserLng());
		Map<String,Long> resultMap = new HashMap<>(1);
		resultMap.put("orderId",orderId);
		return Response.status(Response.Status.CREATED).entity(resultMap).build();
	}

	/**
	 * 查询设备常见问题
	 */
	@GET
	@Path("/faq")
	@Produces(MediaType.APPLICATION_JSON)
	@NoAuth
	public Pagination queryDeviceFaq(@Valid @BeanParam PageRequest request) {
		logger.debug("----DeviceResource.queryDeviceFaq()----");
		return deviceService.queryDeviceFaq(request.getPageNo(),request.getPageSize());
	}

}