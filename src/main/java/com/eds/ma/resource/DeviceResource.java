package com.eds.ma.resource;

import com.eds.ma.bis.device.service.IDeviceService;
import com.eds.ma.bis.device.vo.DeviceInfoVo;
import com.eds.ma.config.SysConfig;
import com.eds.ma.resource.request.DeviceDepositPrePayRequest;
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
			request.setDistance(5000);
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
     * @param request
     */
    @POST
    @Path("/deposit/pay")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deviceDepositPrepay(@Valid DeviceDepositPrePayRequest request){
        logger.debug("WxMaResource.depositPrepay({},{})",super.getOpenId(),request);
        deviceService.deviceDepositPrepay(super.getOpenId(),request.getDeviceId(), request.getUserLat(),request.getUserLng());
        return Response.noContent().build();
    }
}