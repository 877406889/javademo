package com.mmall.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.ICartService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.CartProductVo;
import com.mmall.vo.CartVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;

@Service("iCartService")
public class CartServiceImpl implements ICartService {
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;

    //添加产品到购物车中
    public ServerResponse<CartVo> add(Integer userId,Integer productId,Integer count){
        if (productId==null||count==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart= cartMapper.selectCartByUserIdProductId(userId,productId);
        if(cart==null){
            //这个产品不在这个购物车理，需要新增一个这个产品的记录
            Cart cartItem=new Cart();
            cartItem.setQuantity(count);
            cartItem.setChecked(Const.Cart.CHECKED);
            cartItem.setProductId(productId);
            cartItem.setUserId(userId);
            cartMapper.insert(cartItem);
        }else {
            //如果查询到这个产品已经存在购物车里则只需数量相加
            count=cart.getQuantity()+count;
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        return this.list(userId);
    }
    //更新购物车中产品的数量
    public ServerResponse<CartVo> update(Integer userId,Integer productId,Integer count){
        if(productId==null||count==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart=cartMapper.selectCartByUserIdProductId(userId,productId);
        if(cart!=null){
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        return this.list(userId);
    }
    //删除购物车中的产品
    public ServerResponse<CartVo> deleteProduct(Integer userId,String productIds){
        List<String> productList= Splitter.on(",").splitToList(productIds);
        if(CollectionUtils.isEmpty(productList)){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        cartMapper.deleteByUserIdProductIds(userId,productList);
        return this.list(userId);
    }
    //查询购物车中的产品
    public ServerResponse<CartVo> list(Integer userId){
        CartVo cartVo=this.getCartVoLimit(userId);
        return  ServerResponse.createBySuccess(cartVo);
    }
    //全选
    public ServerResponse<CartVo> selectOrUnSelectAll(Integer userId,Integer productId,Integer checked){
        cartMapper.checkedOrUncheckedProduct(userId,null,checked);
        return this.list(userId);
    }
    //购物车中产品数量
    public ServerResponse<Integer> getCartProductCount(Integer userId){
        if(userId==null){
            return ServerResponse.createBySuccess(0);
        }
        return ServerResponse.createBySuccess(cartMapper.selectCartProductCount(userId));
    }









    private CartVo getCartVoLimit(Integer userId){
        CartVo cartVo=new CartVo();
        //购物车表单中存用户id和product的id
        List<Cart> cartList=cartMapper.selectCartByUserId(userId);
        List<CartProductVo> cartProductVoList= Lists.newArrayList();
        BigDecimal cartTotalPrice=new BigDecimal("0");
        if(CollectionUtils.isNotEmpty(cartList)){
            for (Cart cartItem:cartList){
                CartProductVo cartProductVo=new CartProductVo();
                cartProductVo.setId(cartItem.getId());
                cartProductVo.setProductId(cartItem.getProductId());
                cartProductVo.setUserId(userId);

                Product product=productMapper.selectByPrimaryKey(cartItem.getProductId());
                if(product!=null){
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductStock(product.getStock());
                    //判断库存
                    int buyLimitCount=0;
                    if(product.getStock()>=cartItem.getQuantity()){
                        buyLimitCount = cartItem.getQuantity();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                    }else{
                        buyLimitCount=product.getStock();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                        //购物车中更新有效库存
                        Cart cartForQuantity=new Cart();
                        cartForQuantity.setId(cartItem.getId());
                        cartForQuantity.setQuantity(buyLimitCount);
                        cartMapper.updateByPrimaryKeySelective(cartForQuantity);
                    }
                    cartProductVo.setQuantity(buyLimitCount);
                    //计算某一项产品的总价
                    cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cartProductVo.getQuantity()));
                    //更改产品的勾选状态
                    cartProductVo.setProductChecked(cartItem.getChecked());
                }
                //计算整个购物车的总价
                if (cartItem.getChecked()==Const.Cart.CHECKED){
                    cartTotalPrice=BigDecimalUtil.add(cartTotalPrice.doubleValue(),cartProductVo.getProductTotalPrice().doubleValue());
                }
                cartProductVoList.add(cartProductVo);
            }
        }
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setAllChecked(this.getAllCheckedStatus(userId));
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        return cartVo;
    }

    private boolean getAllCheckedStatus(Integer userId){
        if(userId==null){
            return false;
        }
        return cartMapper.selectCartProductCheckedStatusByUserId(userId)==0;
    }
}
