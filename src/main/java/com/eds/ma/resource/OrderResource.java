package com.eds.ma.resource;

import com.eds.ma.bis.order.service.IOrderService;
import com.eds.ma.bis.order.vo.OrderDetailVo;
import com.eds.ma.resource.request.PageRequest;
import com.xcrm.common.page.Pagination;
import com.xcrm.log.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
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
	public Pagination queryOrders(@QueryParam("orderStatus") String orderStatus, @BeanParam PageRequest pageRequest) {
		logger.debug("----OrderResource.queryOrders({},{})",orderStatus,super.getOpenId());
		return orderService.queryOrders(orderStatus,super.getUserId(),pageRequest.getPageNo(),pageRequest.getPageSize());
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

        OrderDetailVo orderDetailVO = orderService.queryOrderDetail(super.getUserId(),orderId);
        logger.debug("OrderResource.queryOrderDetail result:[{}]",orderDetailVO);
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
        Long orderId = orderService.queryLatestOrderId(super.getUserId());
        Map<String,Long> resultMap = new HashMap<>(1);
        resultMap.put("orderId",orderId);
        return Response.ok(resultMap).build();
    }

    /**
     * 查询最近的使用中订单详情
     * @return  OrderDetailVo   订单详情
     */
    @GET
    @Path("/finance")
    @Produces(MediaType.APPLICATION_JSON)
    public Pagination queryTrnasFinanceIncome(@BeanParam PageRequest pageRequest) {
        logger.debug("OrderResource.queryTrnasFinanceIncome({},{})",super.getOpenId(),pageRequest);
        return orderService.queryTrnasFinanceIncome(super.getUserId(),pageRequest.getPageNo(),pageRequest.getPageSize());
    }



}