package com.commerce.controller.order;

import com.commerce.controller.cart.schema.CartMapper;
import com.commerce.exception.ProductNotFoundException;
import com.commerce.manager.CartManager;
import com.commerce.model.session.Cart;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.resources.preference.Preference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
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
                            .unitPrice(new BigDecimal("4000"))
                            .build();
            List<PreferenceItemRequest> items = new ArrayList<>();
            items.add(itemRequest);
            // Criando a solicitação de preferência
            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(items)
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
    public void handleWebhook(@RequestBody String payload) {
        System.out.println("Recebido webhook: " + payload);
    }


}
