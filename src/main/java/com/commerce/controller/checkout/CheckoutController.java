package com.commerce.controller.checkout;

import com.commerce.controller.order.schema.CustomerOrderMapper;
import com.commerce.exception.CheckoutException;
import com.commerce.manager.CartManager;
import com.commerce.manager.CheckoutManager;
import com.commerce.manager.CustomerOrderManager;
import com.commerce.model.CustomerOrder;
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

    private static final Logger logger = LoggerFactory.getLogger(CheckoutController.class);

    @Value("${custom.MP_PUBLIC_KEY}")
    private String mpPublicKey;

    public CheckoutController(CustomerOrderManager customerOrderManager, CartManager cartManager, CheckoutManager checkoutManager) {
        this.customerOrderManager = customerOrderManager;
        this.checkoutManager = checkoutManager;
    }


    @GetMapping("/payment/order/{id}")
    public String doPayment(Model model, @PathVariable String id) {
        try {
            CustomerOrder customerOrder = customerOrderManager.getOrder(id);
            checkoutManager.createPreference(customerOrder);
            CustomerOrderMapper.GetCustomerOrder data = CustomerOrderMapper.INSTANCE.toGetCustomerOrder(customerOrder);
            model.addAttribute("mpPublicKey", mpPublicKey);
            model.addAttribute("data", data);
            model.addAttribute("template", "checkout/payment");
            return "layout";
        } catch (CheckoutException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error/error";
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
        System.out.println("Retorno webhook: " + payload);
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

        try {
            CustomerOrder data = updateMerchantOrderId(external_reference, merchant_order_id);
            logger.trace(data.getMerchantOrderId());
            model.addAttribute("data", data);
            model.addAttribute("template", "checkout/back-urls/success");
            return "layout";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error/error";
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

        try {
            CustomerOrder data = updateMerchantOrderId(external_reference, merchant_order_id);
            logger.trace(data.getMerchantOrderId());
            model.addAttribute("data", data);
            model.addAttribute("template", "checkout/back-urls/success");
            return "layout";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error/error";
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

        try {
            CustomerOrder data = updateMerchantOrderId(external_reference, merchant_order_id);
            logger.trace(data.getMerchantOrderId());
            model.addAttribute("data", data);
            model.addAttribute("template", "checkout/back-urls/success");
            return "layout";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error/error";
        }

    }


}
