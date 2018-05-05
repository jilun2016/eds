package com.eds.ma.resource;

import com.eds.ma.bis.order.service.IOrderService;
import com.eds.ma.bis.order.vo.OrderDetailVo;
import com.eds.ma.bis.order.vo.OrderVo;
import com.eds.ma.bis.user.vo.UserInfoVo;
import com.eds.ma.bis.wx.service.IWxMaService;
import com.eds.ma.config.SysConfig;
import com.eds.ma.resource.request.PageRequest;
import com.eds.ma.rest.integration.annotation.NoAuth;
import com.xcrm.common.page.Pagination;
import com.xcrm.log.Logger;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 微信小程序订单资源
 * @Author gaoyan
 * @Date: 2018/4/14 0014
 */
@Path("/order")
public class OrderResource extends BaseAuthedResource {
	
	private static Logger logger = Logger.getLogger(OrderResource.class);

	@Autowired
	private IOrderService orderService;

	/**
	 * 查询用户订单列表
	 * @param orderStatus
	 * @return
	 */
	@GET
	@Path("/orders")
	@Produces(MediaType.APPLICATION_JSON)
	public Pagination queryOrders(@NotEmpty(message="订单状态不允许为空") @QueryParam("orderStatus") String orderStatus, @BeanParam PageRequest pageRequest) {
		logger.debug("----OrderResource.queryOrders({},{})",orderStatus,super.getOpenId());
		return orderService.queryOrders(orderStatus,super.getUser(),pageRequest.getPageNo(),pageRequest.getPageSize());
	}

    /**
     * 查询订单详情
     * @param   orderId         订单ID
     * @return  OrderDetailVo   订单详情
     */
    @GET
    @Path("/{orderId}")
    @Produces(MediaType.APPLICATION_JSON)
    public OrderDetailVo queryOrderDetail(@NotNull(message="订单ID不允许为空") @PathParam("orderId") Long orderId) {
        logger.debug("OrderResource.queryOrderDetail({},{})",super.getOpenId(), orderId);

        OrderDetailVo orderDetailVO = orderService.queryOrderDetail(super.getUser(),orderId);
        if(orderDetailVO == null) {
            throw new NotFoundException("未查询到该订单详情");
        } else {
            return orderDetailVO;
        }
    }

    /**
     * 查询最近的使用中订单详情
     * @return  OrderDetailVo   订单详情
     */
    @GET
    @Path("/latest")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryLatestOrderId() {
        logger.debug("OrderResource.queryLatestOrderId({})",super.getOpenId());
        Long orderId = orderService.queryLatestOrderId(super.getUser());
        Map<String,Long> resultMap = new HashMap<>(1);
        resultMap.put("orderId",orderId);
        return Response.ok(resultMap).build();
    }



}