package com.shoppingmall.goods.Service;

import com.shoppingmall.goods.Repository.CustomerRepository;
import com.shoppingmall.goods.entity.Customer;
import com.shoppingmall.goods.exception.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock CustomerRepository customerRepository;

    @InjectMocks CustomerService customerService;

    @Test
    @DisplayName("존재하는 사용자 조회 성공")
    void findById_success() {
        Customer customer = new Customer("user1", "encoded", "홍길동", LocalDate.now(), "USER");
        given(customerRepository.findById("user1")).willReturn(Optional.of(customer));

        Customer result = customerService.findById("user1");

        assertThat(result.getUserId()).isEqualTo("user1");
        assertThat(result.getUserName()).isEqualTo("홍길동");
    }

    @Test
    @DisplayName("존재하지 않는 사용자 조회 시 NotFoundException 발생")
    void findById_notFound() {
        given(customerRepository.findById("ghost")).willReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.findById("ghost"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("사용자를 찾을 수 없습니다");
    }

    @Test
    @DisplayName("아이디 중복 확인 - 존재하면 true 반환")
    void existsById_returnsTrue() {
        given(customerRepository.existsById("user1")).willReturn(true);

        assertThat(customerService.existsById("user1")).isTrue();
    }

    @Test
    @DisplayName("아이디 중복 확인 - 없으면 false 반환")
    void existsById_returnsFalse() {
        given(customerRepository.existsById("newuser")).willReturn(false);

        assertThat(customerService.existsById("newuser")).isFalse();
    }
}
