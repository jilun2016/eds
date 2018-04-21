package com.eds.ma.resource;

import com.eds.ma.bis.common.entity.EdsConfig;
import com.eds.ma.bis.common.service.IEdsConfigService;
import com.eds.ma.bis.device.service.IDeviceService;
import com.eds.ma.bis.device.vo.DeviceInfoVo;
import com.eds.ma.config.SysConfig;
import com.eds.ma.resource.request.DeviceDepositPrePayRequest;
import com.eds.ma.resource.request.DeviceRentRequest;
import com.eds.ma.resource.request.SearchDeviceRequest;
import com.eds.ma.util.DistanceUtil;
import com.xcrm.log.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
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

	@Autowired
	private SysConfig sysConfig;

	/**
	 * 查询指定范围的设备
	 * @param request 查询条件
	 */
	@GET
	@Path("/nearby/devices")
	@Produces(MediaType.APPLICATION_JSON)
	public DeviceInfoVo queryNearbyDevices(@BeanParam SearchDeviceRequest request) {
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
     * 微信押金支付
     */
    @POST
    @Path("/deposit/pay")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deviceDepositPrepay(){
        logger.debug("WxMaResource.depositPrepay({})",super.getOpenId());
        Map<String, Object> paySignMap = deviceService.deviceDepositPrepay(super.getOpenId());
        return Response.ok(paySignMap).build();
    }

	/**
	 * 租借设备
	 * @param request
	 */
	@POST
	@Path("/device/rent")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deviceRent(@Valid DeviceRentRequest request) {
		logger.debug("WxMaResource.deviceRent({},{})",super.getOpenId(), request);
        deviceService.deviceRent(request.getDeviceId(),super.getOpenId(),request.getUserLat(),request.getUserLng());
		return Response.status(Response.Status.CREATED).build();
	}

}