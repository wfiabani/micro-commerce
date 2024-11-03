package com.commerce.controller.checkout;

import com.commerce.controller.order.schema.CustomerOrderMapper;
import com.commerce.exception.ProductNotFoundException;
import com.commerce.manager.CartManager;
import com.commerce.manager.CustomerOrderManager;
import com.commerce.model.CustomerOrder;
import com.commerce.model.CustomerOrderItem;
import com.commerce.model.Product;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.*;
import com.mercadopago.resources.preference.Preference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("checkout")
@Validated
public class CheckoutController {

    private final CustomerOrderManager customerOrderManager;

    private final CartManager cartManager;

    private static final Logger logger = LoggerFactory.getLogger(CheckoutController.class);

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

    public CheckoutController(CustomerOrderManager customerOrderManager, CartManager cartManager) {
        this.customerOrderManager = customerOrderManager;
        this.cartManager = cartManager;
    }


    // refatorar, tirar essa responsabilidade do controller, colocar nas boas práticas
    @GetMapping("/payment/order/{id}")
    public String doPayment(Model model, @PathVariable String id) {
        try {
            CustomerOrder customerOrder = customerOrderManager.getOrder(id);
            CustomerOrderMapper.GetCustomerOrder data = CustomerOrderMapper.INSTANCE.toGetCustomerOrder(customerOrder);
            model.addAttribute("data", data);
            model.addAttribute("template", "checkout/payment");

            if (activeProfile == "default") {
                return "";
            }

            try {
                model.addAttribute("mpPublicKey", mpPublicKey);
                MercadoPagoConfig.setAccessToken(mpAccessToken);
                List<PreferenceItemRequest> items = new ArrayList<>();
                for(CustomerOrderItem item : customerOrder.getItems()){
                    Product product = item.getProduct();
                    PreferenceItemRequest itemRequest =
                            PreferenceItemRequest.builder()
                                    .id(product.getId().toString())
                                    .title(product.getName())
                                    .description(product.getShortDescription())
                                    .categoryId(product.getCategory().getName())
                                    .quantity(item.getQuantity())
                                    .currencyId("BRL")
                                    .unitPrice(item.getUnitPrice())
                                    .build();
                    items.add(itemRequest);

                }
                PreferenceBackUrlsRequest backUrls =
                        PreferenceBackUrlsRequest.builder()
                                .success(siteBaseurl + "/checkout/success")
                                .pending(siteBaseurl + "/checkout/pending")
                                .failure(siteBaseurl + "/checkout/failure")
                                .build();

                PreferenceShipmentsRequest shipments = PreferenceShipmentsRequest.builder().
                        cost(customerOrder.getShippingValue()).build();

                PreferencePayerRequest payer = PreferencePayerRequest.builder()
                        .name(customerOrder.getFullName())
                        .email(customerOrder.getEmail())
                        .build();

                PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                        .items(items)
                        .backUrls(backUrls)
                        .shipments(shipments)
                        .externalReference(customerOrder.getOrderIdentifier())
                        .additionalInfo(customerOrder.getOrderIdentifier())
                        .payer(payer)
                        .build();

                PreferenceClient client = new PreferenceClient();
                Preference preference = client.create(preferenceRequest);
                model.addAttribute("preferenceId", preference.getId());
                model.addAttribute("template", "checkout/payment");
                return "layout";
            } catch (ProductNotFoundException e) {
                model.addAttribute("errorMessage", e.getMessage());
                return "error/error";
            } catch (Exception e) {
                model.addAttribute("errorMessage", e.getMessage());
                return "error/error";
            }

        } catch (ProductNotFoundException e) {
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



    private CustomerOrder updateMerchantOrderId(String external_reference, String merchant_order_id) throws Exception{
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

        System.out.println("Collection ID: " + collection_id);
        System.out.println("Collection Status: " + collection_status);
        System.out.println("Payment ID: " + payment_id);
        System.out.println("Status: " + status);
        System.out.println("External Reference: " + external_reference);
        System.out.println("Payment Type: " + payment_type);
        System.out.println("Merchant Order ID: " + merchant_order_id);
        System.out.println("Preference ID: " + preference_id);
        System.out.println("Site ID: " + site_id);
        System.out.println("Processing Mode: " + processing_mode);
        System.out.println("Merchant Account ID: " + merchant_account_id);

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

        System.out.println("Collection ID: " + collection_id);
        System.out.println("Collection Status: " + collection_status);
        System.out.println("Payment ID: " + payment_id);
        System.out.println("Status: " + status);
        System.out.println("External Reference: " + external_reference);
        System.out.println("Payment Type: " + payment_type);
        System.out.println("Merchant Order ID: " + merchant_order_id);
        System.out.println("Preference ID: " + preference_id);
        System.out.println("Site ID: " + site_id);
        System.out.println("Processing Mode: " + processing_mode);
        System.out.println("Merchant Account ID: " + merchant_account_id);

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

        System.out.println("Collection ID: " + collection_id);
        System.out.println("Collection Status: " + collection_status);
        System.out.println("Payment ID: " + payment_id);
        System.out.println("Status: " + status);
        System.out.println("External Reference: " + external_reference);
        System.out.println("Payment Type: " + payment_type);
        System.out.println("Merchant Order ID: " + merchant_order_id);
        System.out.println("Preference ID: " + preference_id);
        System.out.println("Site ID: " + site_id);
        System.out.println("Processing Mode: " + processing_mode);
        System.out.println("Merchant Account ID: " + merchant_account_id);

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
