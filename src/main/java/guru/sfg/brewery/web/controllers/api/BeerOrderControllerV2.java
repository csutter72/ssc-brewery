package guru.sfg.brewery.web.controllers.api;

import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.security.annotation.BeerOrderReadPermissionV2;
import guru.sfg.brewery.services.BeerOrderService;
import guru.sfg.brewery.web.model.BeerOrderDto;
import guru.sfg.brewery.web.model.BeerOrderPagedList;
import lombok.extern.slf4j.Slf4j;

/**
 * Beer Order Controller
 */
@Slf4j
@RequestMapping("/api/v2/orders")
@RestController
public class BeerOrderControllerV2 {

    private static final Integer DEFAULT_PAGE_NUMBER = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 25;

    private final BeerOrderService beerOrderService;

    public BeerOrderControllerV2(BeerOrderService beerOrderService) {
        this.beerOrderService = beerOrderService;
    }

    @BeerOrderReadPermissionV2
    @GetMapping
    public BeerOrderPagedList listOrders(@AuthenticationPrincipal User user,
                                         @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                         @RequestParam(value = "pageSize", required = false) Integer pageSize){

        
        if (pageNumber == null || pageNumber < 0){
            pageNumber = DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        if(user.getCustomer() != null) {
             return beerOrderService.listOrders(user.getCustomer().getId(), PageRequest.of(pageNumber, pageSize));
        }  else {
            return beerOrderService.listOrders(PageRequest.of(pageNumber, pageSize));
        }
    }

    @BeerOrderReadPermissionV2
    @GetMapping("/{orderId}")
    public BeerOrderDto getOrder(@PathVariable("orderId") UUID orderId){

        try {
            BeerOrderDto beerOrderDto = beerOrderService.getOrderById(orderId);
            log.debug("Beer Order found: " + beerOrderDto.toString());
            return beerOrderDto;
        } catch(Exception ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
        }
    }
}