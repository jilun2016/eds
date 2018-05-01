package com.eds.ma.resource;

import com.eds.ma.bis.device.service.IDeviceService;
import com.eds.ma.bis.device.vo.IdleDeviceVo;
import com.xcrm.log.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * 店铺资源
 * @Author gaoyan
 * @Date: 2018/3/31
 */
@Path("/sp")
public class SpResource extends BaseAuthedResource{
	
	private static Logger logger = Logger.getLogger(SpResource.class);

	@Autowired
	private IDeviceService deviceService;


	/**
	 * 查询店铺所属的设备
	 * @param spId 店铺id
	 */
	@GET
	@Path("/{spId}/devices")
	@Produces(MediaType.APPLICATION_JSON)
	public List<IdleDeviceVo> querySpDevices(@NotNull(message = "店铺ID不允许为空") @PathParam("spId") Long spId) {
		logger.debug("----DeviceResource.querySpDevices({},{})----",super.getOpenId(),spId);
        return deviceService.queryIdleDeviceListBySpId(spId);
	}

}