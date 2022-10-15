package guru.sfg.brewery.web.controllers.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import guru.sfg.brewery.web.controllers.BaseIT;

@SpringBootTest
public class BeerRestControllerIT extends BaseIT {

    @Test
    void findBeers() throws Exception {
        mockMvc.perform(get("/api/v1/beer"))
            .andExpect(status().isOk());
    }

    @Test
    void findBeerById() throws Exception {
        mockMvc.perform(get("/api/v1/beer/a7504341-8604-42c7-9845-02910f682ffa"))
            .andExpect(status().isOk());
    }

    @Test
    void findBeerByUpc() throws Exception {
        mockMvc.perform(get("/api/v1/beerUpc/0631234300019"))
            .andExpect(status().isOk());
    }
}
