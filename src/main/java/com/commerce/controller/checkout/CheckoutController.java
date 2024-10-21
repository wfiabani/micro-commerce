package com.commerce.controller.checkout;

import com.commerce.controller.order.schema.CustomerOrderMapper;
import com.commerce.exception.ProductNotFoundException;
import com.commerce.manager.CartManager;
import com.commerce.manager.CustomerOrderManager;
import com.commerce.model.CustomerOrder;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.*;
import com.mercadopago.resources.preference.Preference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("checkout")
@Validated
public class CheckoutController {

    private final CustomerOrderManager customerOrderManager;
    private final CartManager cartManager;
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

    public CheckoutController(CustomerOrderManager customerOrderManager, CartManager cartManager){
        this.customerOrderManager = customerOrderManager;
        this.cartManager = cartManager;
    }

    @GetMapping("/payment/order/{id}")
    public String doPayment(Model model, @PathVariable String id) {
        try {
            CustomerOrder customerOrder = customerOrderManager.getOrder(id);
            CustomerOrderMapper.GetCustomerOrder data = CustomerOrderMapper.INSTANCE.toGetCustomerOrder(customerOrder);
            model.addAttribute("data", data);
            model.addAttribute("template", "checkout/payment");

            if(activeProfile=="default"){
                return "";
            }

            try {
                model.addAttribute("mpPublicKey", mpPublicKey);
                MercadoPagoConfig.setAccessToken(mpAccessToken);
                PreferenceItemRequest itemRequest =
                        PreferenceItemRequest.builder()
                                .id("1234")
                                .title("Games")
                                .description("PS5")
                                .pictureUrl("http://picture.com/PS5")
                                .categoryId("games")
                                .quantity(2)
                                .currencyId("BRL")
                                .unitPrice(new BigDecimal("4"))
                                .build();
                List<PreferenceItemRequest> items = new ArrayList<>();
                items.add(itemRequest);

                PreferenceBackUrlsRequest backUrls =
                        PreferenceBackUrlsRequest.builder()
                                .success(siteBaseurl + "/pedido/success")
                                .pending(siteBaseurl + "/pedido/pending")
                                .failure(siteBaseurl + "/pedido/failure")
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



    @GetMapping("/success")
    @ResponseBody
    public String handleWebhookSuccess(Model model, @RequestBody(required = false) String payload) {
        System.out.println("Recebido: " + payload);
        model.addAttribute("template", "checkout/back-urls/success");
        return "layout";
    }

    @GetMapping("/pending")
    @ResponseBody
    public String handleWebhookPending(Model model, @RequestBody(required = false) String payload) {
        System.out.println("Recebido: " + payload);
        model.addAttribute("template", "checkout/back-urls/pending");
        return "layout";
    }

    @GetMapping("/failure")
    @ResponseBody
    public String handleWebhookFailure(Model model, @RequestBody(required = false) String payload) {
        System.out.println("Recebido: " + payload);
        model.addAttribute("template", "checkout/back-urls/failure");
        return "layout";
    }



}
