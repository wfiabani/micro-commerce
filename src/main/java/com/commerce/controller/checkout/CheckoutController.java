package com.commerce.controller.checkout;

import com.commerce.controller.order.schema.CustomerOrderMapper;
import com.commerce.exception.CheckoutException;
import com.commerce.manager.CartManager;
import com.commerce.manager.CheckoutManager;
import com.commerce.manager.CustomerOrderManager;
import com.commerce.model.CustomerOrder;
import com.commerce.util.TemplateConstants;
import com.mercadopago.resources.preference.Preference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("checkout")
@Validated
public class CheckoutController {

    private final CustomerOrderManager customerOrderManager;

    private final CheckoutManager checkoutManager;

    private final CartManager cartManager;

    private static final Logger logger = LoggerFactory.getLogger(CheckoutController.class);

    @Value("${custom.MP_PUBLIC_KEY}")
    private String mpPublicKey;

    public CheckoutController(CustomerOrderManager customerOrderManager, CartManager cartManager, CheckoutManager checkoutManager) {
        this.customerOrderManager = customerOrderManager;
        this.checkoutManager = checkoutManager;
        this.cartManager = cartManager;
    }


    @GetMapping("/payment/order/{id}")
    public String doPayment(Model model, @PathVariable String id) {
        try {
            CustomerOrder customerOrder = customerOrderManager.getOrder(id);
            Preference preference = checkoutManager.createPreference(customerOrder);
            CustomerOrderMapper.GetCustomerOrder data = CustomerOrderMapper.INSTANCE.toGetCustomerOrder(customerOrder);
            model.addAttribute("preferenceId", preference.getId());
            model.addAttribute("mpPublicKey", mpPublicKey);
            model.addAttribute("data", data);
            model.addAttribute("template", TemplateConstants.CHECKOUT_PAYMENT);
            return TemplateConstants.LAYOUT;
        } catch (CheckoutException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return TemplateConstants.ERROR;
        }
    }


    @PostMapping("/webhooks")
    @ResponseBody
    public void handleWebhook(
            @RequestBody String payload,
            @RequestHeader("x-signature") String xSignature,
            @RequestHeader("x-request-id") String xRequestId,
            @RequestParam Map<String, String> queryParams
    ) {
        logger.info("Webhook chamado {}", payload);
    }


    private CustomerOrder updateMerchantOrderId(String external_reference, String merchant_order_id) throws Exception {
        return customerOrderManager.updateMerchantOrderId(external_reference, merchant_order_id);
    }

    @GetMapping("/success")
    public String handleWebhookSuccess(Model model,
                                       @RequestParam(required = false) String collection_id,
                                       @RequestParam(required = false) String collection_status,
                                       @RequestParam(required = false) String payment_id,
                                       @RequestParam(required = false) String status,
                                       @RequestParam(required = false) String external_reference,
                                       @RequestParam(required = false) String payment_type,
                                       @RequestParam(required = false) String merchant_order_id,
                                       @RequestParam(required = false) String preference_id,
                                       @RequestParam(required = false) String site_id,
                                       @RequestParam(required = false) String processing_mode,
                                       @RequestParam(required = false) String merchant_account_id) {

//        System.out.println("Collection ID: " + collection_id);
//        System.out.println("Collection Status: " + collection_status);
//        System.out.println("Payment ID: " + payment_id);
//        System.out.println("Status: " + status);
//        System.out.println("External Reference: " + external_reference);
//        System.out.println("Payment Type: " + payment_type);
//        System.out.println("Merchant Order ID: " + merchant_order_id);
//        System.out.println("Preference ID: " + preference_id);
//        System.out.println("Site ID: " + site_id);
//        System.out.println("Processing Mode: " + processing_mode);
//        System.out.println("Merchant Account ID: " + merchant_account_id);
        logger.info("URL success de callback chamada merchant_account_id={} preference_id={} merchant_order_id={} external_reference={} status={}", merchant_account_id, preference_id, merchant_order_id, external_reference, status);
        try {
            cartManager.clear();
            CustomerOrder data = updateMerchantOrderId(external_reference, merchant_order_id);
            logger.trace(data.getMerchantOrderId());
            model.addAttribute("data", data);
            model.addAttribute("template", TemplateConstants.CHECKOUT_BACKURL_SUCCESS);
            return TemplateConstants.LAYOUT;
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return TemplateConstants.ERROR;
        }

    }

    @GetMapping("/pending")
    public String handleWebhookPending(Model model,
                                       @RequestParam(required = false) String collection_id,
                                       @RequestParam(required = false) String collection_status,
                                       @RequestParam(required = false) String payment_id,
                                       @RequestParam(required = false) String status,
                                       @RequestParam(required = false) String external_reference,
                                       @RequestParam(required = false) String payment_type,
                                       @RequestParam(required = false) String merchant_order_id,
                                       @RequestParam(required = false) String preference_id,
                                       @RequestParam(required = false) String site_id,
                                       @RequestParam(required = false) String processing_mode,
                                       @RequestParam(required = false) String merchant_account_id) {

        logger.info("URL pending de callback chamada merchant_account_id={} preference_id={} merchant_order_id={} external_reference={} status={}", merchant_account_id, preference_id, merchant_order_id, external_reference, status);
        try {
            cartManager.clear();
            CustomerOrder data = updateMerchantOrderId(external_reference, merchant_order_id);
            logger.trace(data.getMerchantOrderId());
            model.addAttribute("data", data);
            model.addAttribute("template", TemplateConstants.CHECKOUT_BACKURL_PENDING);
            return TemplateConstants.LAYOUT;
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return TemplateConstants.ERROR;
        }

    }

    @GetMapping("/failure")
    public String handleWebhookFailure(Model model,
                                       @RequestParam(required = false) String collection_id,
                                       @RequestParam(required = false) String collection_status,
                                       @RequestParam(required = false) String payment_id,
                                       @RequestParam(required = false) String status,
                                       @RequestParam(required = false) String external_reference,
                                       @RequestParam(required = false) String payment_type,
                                       @RequestParam(required = false) String merchant_order_id,
                                       @RequestParam(required = false) String preference_id,
                                       @RequestParam(required = false) String site_id,
                                       @RequestParam(required = false) String processing_mode,
                                       @RequestParam(required = false) String merchant_account_id) {

        logger.info("URL failure de callback chamada merchant_account_id={} preference_id={} merchant_order_id={} external_reference={} status={}", merchant_account_id, preference_id, merchant_order_id, external_reference, status);
        try {
            cartManager.clear();
            CustomerOrder data = updateMerchantOrderId(external_reference, merchant_order_id);
            logger.trace(data.getMerchantOrderId());
            model.addAttribute("data", data);
            model.addAttribute("template", TemplateConstants.CHECKOUT_BACKURL_FAILURE);
            return TemplateConstants.LAYOUT;
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return TemplateConstants.ERROR;
        }

    }


}
