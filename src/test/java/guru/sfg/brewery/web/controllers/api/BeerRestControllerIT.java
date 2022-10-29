package guru.sfg.brewery.web.controllers.api;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Random;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;

import guru.sfg.brewery.domain.Beer;
import guru.sfg.brewery.repositories.BeerRepository;
import guru.sfg.brewery.web.controllers.BaseIT;
import guru.sfg.brewery.web.model.BeerStyleEnum;

@SpringBootTest
public class BeerRestControllerIT extends BaseIT {

    @Autowired
    private BeerRepository beerRepository;

    private Beer beerToDelete() {
        final Random rand = new Random();
        
        return beerRepository.saveAndFlush(Beer.builder()
            .beerName("DeleteMeBeer")
            .beerStyle(BeerStyleEnum.ALE)
            .minOnHand(12)
            .quantityToBrew(200)
            .upc(String.valueOf(rand.nextInt(9999999)))
            .build());
    }

    @Test
    void listBreweriesCustomerRole() throws Exception {
        mockMvc.perform(get("/brewery/api/v1/breweries")
            .with(httpBasic("scott", "tiger")))
            .andExpect(status().isOk());
    }

    @Test
    void listBreweriesCustomerUser() throws Exception {
        mockMvc.perform(get("/brewery/api/v1/breweries")
            .with(httpBasic("user", "password")))
            .andExpect(status().isForbidden());
    }

    @Test
    void listBreweriesCustomerAdmin() throws Exception {
        mockMvc.perform(get("/brewery/api/v1/breweries")
            .with(httpBasic("spring", "pwd")))
            .andExpect(status().is2xxSuccessful());
    }

    @Test
    void listBreweriesNoAuth() throws Exception {
        mockMvc.perform(get("/brewery/api/v1/breweries"))
            .andExpect(status().isUnauthorized());
    }


    @Test
    void findBeersAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/beer")
            .with(httpBasic("spring", "pwd")))
            .andExpect(status().isOk());
    }

    @Test
    void findBeersUser() throws Exception {
        mockMvc.perform(get("/api/v1/beer")
            .with(httpBasic("user", "password")))
            .andExpect(status().isOk());
    }

    @Test
    void findBeersCustomer() throws Exception {
        mockMvc.perform(get("/api/v1/beer")
            .with(httpBasic("scott", "tiger")))
            .andExpect(status().isOk());
    }

    @Test
    void findBeersNoAuth() throws Exception {
        mockMvc.perform(get("/api/v1/beer"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void findBeerByIdNoAuth() throws Exception {
        mockMvc.perform(get("/api/v1/beer/a7504341-8604-42c7-9845-02910f682ffa"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void findBeerByUpcNoAuth() throws Exception {
        mockMvc.perform(get("/api/v1/beerUpc/0631234300019"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void findBeerByUpcADMIN() throws Exception {
        mockMvc.perform(get("/api/v1/beerUpc/0631234300019")
            .with(httpBasic("spring", "pwd")))
            .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("spring")
    void processCreationForm() throws Exception{
        mockMvc.perform(post("/beers/new").with(csrf())
                .with(httpBasic("spring", "pwd")))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void deleteBeerById() throws Exception {
        mockMvc.perform(delete("/api/v1/beer/" + beerToDelete().getId())
            .header("Api-Key", "spring")
            .header("Api-Secret", "pwd"))
            .andExpect(status().is2xxSuccessful());
    }

    @Test
    void deleteBeerHttpBasic() throws Exception{
        mockMvc.perform(delete("/api/v1/beer/" + beerToDelete().getId())
                .with(httpBasic("spring", "pwd")))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void deleteBeerHttpBasicUserRole() throws Exception{
        mockMvc.perform(delete("/api/v1/beer/" + beerToDelete().getId())
                .with(httpBasic("user", "password")))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteBeerHttpBasicCustomerRole() throws Exception{
        mockMvc.perform(delete("/api/v1/beer/" + beerToDelete().getId())
                .with(httpBasic("scott", "tiger")))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteBeerNoAuth() throws Exception{
        mockMvc.perform(delete("/api/v1/beer/" + beerToDelete().getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteBeerByIdBadCredentials() throws Exception {
        mockMvc.perform(delete("/api/v1/beer/" + beerToDelete().getId())
            .header("Api-Key", "spring")
            .header("Api-Secret", "guru"))
            .andExpect(status().isUnauthorized());
    }

    //@Test
    void deleteBeerByIdWithCredentialsFromParameter() throws Exception {
        mockMvc.perform(delete("/api/v1/beer/" + beerToDelete().getId())
            .queryParam("Api-Key", "spring")
            .queryParam("Api-Secret", "pwd"))

            .andExpect(status().is2xxSuccessful());
    }

    @Test
    void findBeerFormAdmin() throws Exception {
        mockMvc.perform(get("/beers").param("beerName", "")
            .with(httpBasic("spring", "pwd")))
            .andExpect(status().isOk());
    }

}
