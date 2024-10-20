package com.commerce.controller.order;

import com.commerce.controller.cart.schema.CartMapper;
import com.commerce.controller.order.schema.CustomerOrderMapper;
import com.commerce.exception.InvalidOrderException;
import com.commerce.exception.OrderProcessingException;
import com.commerce.manager.CartManager;
import com.commerce.manager.CustomerOrderManager;
import com.commerce.model.session.Cart;
import jakarta.validation.Valid;
import org.apache.commons.beanutils.ConversionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Map;


@Controller
@RequestMapping("pedido")
@Validated
public class CustomerOrderController {

    private final CartManager cartManager;
    private final CustomerOrderManager orderManager;

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
        try {
            var customerOrder = orderManager.addCustomerOrder(request);
            var data = CustomerOrderMapper.INSTANCE.toGetCustomerOrder(customerOrder);
            System.out.println(data);
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
        model.addAttribute("template", "order/order-details");
        return "layout";
    }


    @PostMapping("/webhooks")
    @ResponseBody
    public void handleWebhook(
            @RequestBody String payload,
            @RequestHeader("x-signature") String xSignature,
            @RequestHeader("x-request-id") String xRequestId,
            @RequestParam Map<String, String> queryParams
    ) {


        // Extrair data.id dos parâmetros da consulta
        String dataID = queryParams.getOrDefault("data.id", "");

        // Separar a x-signature em partes
        String[] parts = xSignature.split(",");

        // Inicializar variáveis para ts e hash
        String ts = null;
        String hash = null;

        // Iterar sobre os valores para obter ts e v1
        for (String part : parts) {
            String[] keyValue = part.split("=", 2);
            if (keyValue.length == 2) {
                String key = keyValue[0].trim();
                String value = keyValue[1].trim();
                if ("ts".equals(key)) {
                    ts = value;
                } else if ("v1".equals(key)) {
                    hash = value;
                }
            }
        }

        // Gerar a string manifest
        String manifest = "id:" + dataID + ";request-id:" + xRequestId + ";ts:" + ts + ";";

        // Criar a assinatura HMAC
        try {
            String calculatedHash = calculateHMAC(manifest, mpClientSecret);
            if (calculatedHash.equals(hash)) {
                // HMAC verification passed
                System.out.println("Verificada assinatura");
                System.out.println("Retorno webhook: " + payload);
            } else {
                System.out.println("Retorno webhook: " + payload);
                System.out.println("Falohu na verificação");
            }
        } catch (Exception e) {
            System.out.println("Exception");
        }


    }


    private String calculateHMAC(String data, String key) throws Exception {
        Mac hmacSha256 = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        hmacSha256.init(secretKeySpec);
        byte[] hash = hmacSha256.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hash); // Converter o resultado em hexadecimal
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }


    @GetMapping("/success")
    @ResponseBody
    public void handleWebhookSuccess(@RequestBody String payload, Model model) {
        System.out.println("Recebido callback success: " + payload);
    }

    @GetMapping("/pending")
    @ResponseBody
    public void handleWebhookPending(@RequestBody String payload, Model model) {
        System.out.println("Recebido callback pending: " + payload);
    }

    @GetMapping("/failure")
    @ResponseBody
    public void handleWebhookFailure(@RequestBody String payload,
                                     Model model,
                                     @RequestParam(value = "collection_id", required = false) String collectionId,
                                     @RequestParam(value = "collection_status", required = false) String collectionStatus,
                                     @RequestParam(value = "payment_id", required = false) String paymentId,
                                     @RequestParam(value = "status", required = false) String status,
                                     @RequestParam(value = "external_reference", required = false) String externalReference,
                                     @RequestParam(value = "payment_type", required = false) String paymentType,
                                     @RequestParam(value = "merchant_order_id", required = false) String merchantOrderId,
                                     @RequestParam(value = "preference_id", required = false) String preferenceId,
                                     @RequestParam(value = "site_id", required = false) String siteId,
                                     @RequestParam(value = "processing_mode", required = false) String processingMode,
                                     @RequestParam(value = "merchant_account_id", required = false) String merchantAccountId
    ) {

        System.out.println("Collection ID: " + collectionId);
        System.out.println("Collection Status: " + collectionStatus);
        System.out.println("Payment ID: " + paymentId);
        System.out.println("Status: " + status);
        System.out.println("External Reference: " + externalReference);
        System.out.println("Payment Type: " + paymentType);
        System.out.println("Merchant Order ID: " + merchantOrderId);
        System.out.println("Preference ID: " + preferenceId);
        System.out.println("Site ID: " + siteId);
        System.out.println("Processing Mode: " + processingMode);
        System.out.println("Merchant Account ID: " + merchantAccountId);

        System.out.println("Recebido callback failure: " + payload);
    }


}
