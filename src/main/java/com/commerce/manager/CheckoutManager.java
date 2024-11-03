package com.commerce.manager;

import com.commerce.controller.checkout.CheckoutController;
import com.commerce.exception.CheckoutException;
import com.commerce.model.CustomerOrder;
import com.commerce.model.CustomerOrderItem;
import com.commerce.model.Product;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.*;
import com.mercadopago.resources.preference.Preference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

@Service
@Validated
public class CheckoutManager {

    private static final Logger logger = LoggerFactory.getLogger(CheckoutController.class);

    @Value("${custom.MP_ACCESS_TOKEN}")
    private String mpAccessToken;

    @Value("${custom.SITE_BASEURL}")
    private String siteBaseurl;

    public CheckoutManager() {

    }

    public void createPreference(CustomerOrder customerOrder) {
        try {
            MercadoPagoConfig.setAccessToken(mpAccessToken);
            List<PreferenceItemRequest> items = createPreferenceItens(customerOrder);
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
        } catch (Exception e) {
            throw new CheckoutException("Erro ao criar preferência");
        }
    }

    private List<PreferenceItemRequest> createPreferenceItens(CustomerOrder customerOrder){
        List<PreferenceItemRequest> items = new ArrayList<>();
        for (CustomerOrderItem item : customerOrder.getItems()) {
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
        return items;
    }

}
