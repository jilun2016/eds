package com.eds.ma.resource;

import com.eds.ma.bis.user.entity.User;
import com.eds.ma.bis.user.service.IUserService;
import com.eds.ma.bis.user.vo.UserInfoVo;
import com.eds.ma.bis.user.vo.UserWalletVo;
import com.eds.ma.exception.BizCoreRuntimeException;
import com.eds.ma.rest.common.BizErrorConstants;
import com.xcrm.log.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 用户资源
 * @Author gaoyan
 * @Date: 2018/3/31
 */
@Path("/user")
public class UserResource extends BaseAuthedResource{
	
	private static Logger logger = Logger.getLogger(UserResource.class);

	@Autowired
	private IUserService userService;

    @Autowired
    private TaskExecutor taskExecutor;


	@GET
	@Path("/test")
	@Produces(MediaType.APPLICATION_JSON)
	public Response test() throws InterruptedException, ExecutionException {
        List<Integer> taskList = Arrays.asList(1, 2, 3, 4, 5, 6, 7);
        List<String> list2 = new ArrayList<String>();
        CompletableFuture[] cfs = taskList.stream().map(object-> CompletableFuture.supplyAsync(()->{
            if(object == 5){
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return object;
        }, taskExecutor)
                .thenApply(h->Integer.toString(h))
                .whenComplete((v, e) -> {//如需获取任务完成先手顺序，此处代码即可
                    System.out.println("任务"+v+"完成!result="+v+"，异常 e="+e+","+new Date());
                }))
                .toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(cfs).join();//封装后无返回值，必须自己whenComplete()获取
        return Response.ok().build();
	}

    /**
     * 查询用户基础信息
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public UserInfoVo queryUserInfo() {
        logger.debug("UserResource.queryUserInfo({})", super.getOpenId());
        User user = userService.checkUserExist(super.getOpenId());
        UserInfoVo userInfoVo = new UserInfoVo();
        userInfoVo.setHeadimgurl(user.getHeadimgurl());
        userInfoVo.setNickName(user.getNickname());
        userInfoVo.setOpenId(super.getOpenId());
        return userInfoVo;
    }

	/**
	 * 查询用户钱包信息
	 */
	@GET
	@Path("/wallet")
	@Produces(MediaType.APPLICATION_JSON)
	public UserWalletVo queryUserWallet() {
		logger.debug("UserResource.queryUserWallet({})", super.getOpenId());
		return userService.queryUserWallet(super.getOpenId());
	}

    /**
     * 用户提现
     * 提现金额 = 余额+押金
     */
    @POST
    @Path("/wallet/withdraw")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response walletWithdraw(){
        logger.debug("UserResource.walletWithdraw({})",super.getOpenId());
        int result = userService.walletWithdraw(super.getOpenId());
        if(result == 0){
            throw new BizCoreRuntimeException(BizErrorConstants.WALLET_WITHDRAW_PART_ERROR);
        }
        return Response.ok().build();
    }
}