package com.ep.accounts;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AccountServiceTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Autowired AccountService accountService;
    @Autowired AccountRepository accountRepository;
    @Autowired PasswordEncoder passwordEncoder;

    @DisplayName("user deatials service test")
    @Test
    void findByUserName(){
        String username = "ep@email.com";
        String password = "123123";

        // given
        Account account = Account.builder()
                .email(username)
                .password(password)
                .roles(Set.of(AccountRole.ADMIN,AccountRole.USER))
                .build();
        accountService.saveAccount(account);

        // when
        UserDetailsService userDetailsService = (UserDetailsService) accountService;
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        /// then
        assertThat(passwordEncoder.matches(password,userDetails.getPassword())).isTrue();
    }


    @DisplayName("username error 테스트")
    @Test
    void findByUsernameFail(){
        // Expected
        String username = "random@email.com";
//        expectedException.expect(UsernameNotFoundException.class);
//        expectedException.expectMessage(Matchers.containsString(username));

        // when
        Exception exception = assertThrows(UsernameNotFoundException.class,() ->{
            accountService.loadUserByUsername(username);
        });

        assertThat(exception.getClass()).isEqualTo(UsernameNotFoundException.class);
        assertThat(exception.getMessage()).contains(username);

        // 예외 테스트
        // 코드가 다소 복잡
//        try{
//            accountService.loadUserByUsername(username);
//            fail();
//        } catch(UsernameNotFoundException e) {
//            assertThat(e.getMessage()).containsSequence(username);
//        }
    }


}