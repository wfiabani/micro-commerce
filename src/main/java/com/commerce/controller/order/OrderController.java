package com.commerce.controller.order;

import com.commerce.controller.cart.schema.CartMapper;
import com.commerce.exception.ProductNotFoundException;
import com.commerce.manager.CartManager;
import com.commerce.model.session.Cart;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.resources.preference.Preference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Controller
@RequestMapping("pedido")
@Validated
public class OrderController {

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

    public OrderController(CartManager cartManager) {
        this.cartManager = cartManager;
    }

    @GetMapping("/detalhes-do-pedido")
    public String getOrderForm(Model model) {

        try {

            /*********************************************************/
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
// ...
                    PreferenceBackUrlsRequest.builder()
                            .success(siteBaseurl+"/pedido/success")
                            .pending(siteBaseurl+"/pedido/pending")
                            .failure(siteBaseurl+"/pedido/failure")
                            .build();

            // Criando a solicitação de preferência
            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(items)
                    .backUrls(backUrls)
                    .build();

            // Criando o cliente de preferência e enviando a solicitação
            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(preferenceRequest);  // Usar
            model.addAttribute("preferenceId", preference.getId());
            /*********************************************************/

            Cart cart = cartManager.getCart();
            var response = CartMapper.INSTANCE.toGetCart(cart);
            model.addAttribute("cart", response);
            model.addAttribute("template", "order/order-details");
            return "layout";
        } catch (ProductNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error/error"; // Redireciona para uma página de erro customizada
        } catch (Exception e) {
            System.out.println(e.toString());
            model.addAttribute("errorMessage", e.getMessage());
            return "error/error"; // Redireciona para uma página de erro customizada
        }
    }




















    @PostMapping("/webhooks")
    @ResponseBody
    public void handleWebhook(
            @RequestBody String payload,
            @RequestHeader("x-signature") String signature
    ) {
        try {
            // Verifica a assinatura recebida
            if (verifySignature(payload, signature, mpClientSecret)) {
                System.out.println("Assinatura válida");
            } else {
                System.out.println("Assinatura inválida");
                return; // Se a assinatura for inválida, você pode retornar uma resposta de erro ou finalizar o processamento aqui.
            }
            
            System.out.println("Recebido webhook: " + payload);

        } catch (Exception e) {
            System.out.println("Erro ao verificar assinatura: " + e.getMessage());
        }
    }

    // Método para verificar a assinatura
    private boolean verifySignature(String payload, String signatureHeader, String secretKey) throws Exception {
        // 1. Configurar o HMAC SHA-256 usando a chave secreta
        Mac hmacSha256 = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        hmacSha256.init(secretKeySpec);

        // 2. Calcular o HMAC do payload (corpo da requisição)
        byte[] hash = hmacSha256.doFinal(payload.getBytes(StandardCharsets.UTF_8));

        // 3. Codificar o hash gerado em base64
        String calculatedSignature = Base64.getEncoder().encodeToString(hash);

        // 4. Comparar a assinatura gerada com a recebida no cabeçalho
        return calculatedSignature.equals(signatureHeader);
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
