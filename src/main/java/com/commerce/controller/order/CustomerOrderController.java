package com.commerce.controller.order;

import com.commerce.controller.cart.schema.CartMapper;
import com.commerce.controller.order.schema.CustomerOrderMapper;
import com.commerce.exception.InvalidOrderException;
import com.commerce.exception.OrderProcessingException;
import com.commerce.manager.CartManager;
import com.commerce.manager.CustomerOrderManager;
import com.commerce.model.session.Cart;
import com.commerce.util.TemplateConstants;
import jakarta.validation.Valid;
import org.apache.commons.beanutils.ConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("pedido")
@Validated
public class CustomerOrderController {

    private final CartManager cartManager;
    private final CustomerOrderManager orderManager;

    private static final Logger logger = LoggerFactory.getLogger(CustomerOrderController.class);

    @Value("${custom.MP_PUBLIC_KEY}")
    private String mpPublicKey;

    @Value("${custom.MP_ACCESS_TOKEN}")
    private String mpAccessToken;

    @Value("${custom.MP_CLIENT_SECRET}")
    private String mpClientSecret;

    @Value("${custom.MP_CLIENT_ID}")
    private String mpClientId;

    @Value("${custom.SITE_BASEURL}")
    private String siteBaseurl;

    @Value("${spring.profiles.active:default}")
    private String activeProfile;
    

    public CustomerOrderController(CustomerOrderManager orderManager, CartManager cartManager) {
        this.orderManager = orderManager;
        this.cartManager = cartManager;
    }

    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<?> addCustomerOrder(@Valid @RequestBody CustomerOrderMapper.PostCustomerOrder request) {
        logger.info("Adicionando pedido {}", request.toString());
        try {
            var customerOrder = orderManager.addCustomerOrder(request);
            var data = CustomerOrderMapper.INSTANCE.toGetCustomerOrder(customerOrder);
            logger.info("Pedido criado {}", data);
            return ResponseEntity.ok(data);
        } catch (InvalidOrderException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (OrderProcessingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (ConversionException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Conversion error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while creating the order.");
        }
    }


    @GetMapping("/detalhes-do-pedido")
    public String getOrderForm(Model model) {
        Cart cart = cartManager.getCart();
        var response = CartMapper.INSTANCE.toGetCart(cart);
        model.addAttribute("cart", response);
        model.addAttribute("template", TemplateConstants.ORDER_DETAILS);
        return TemplateConstants.LAYOUT;
    }


}
