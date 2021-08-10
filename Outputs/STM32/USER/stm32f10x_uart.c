/*

Комплект команд UART под ядро Cortex M3
Автор: Чудаков Максим
email: fontes35@mail.ru

* настройка на скорость 9600 бод
* чип STM32F103C8T6, настройка UART1 A9,A10

*/

#include "stm32f10x.h"                                              // Device header
#include "stm32f10x_uart.h"

void UART_INIT(void)
{
    RCC->APB2ENR|=RCC_APB2ENR_IOPAEN|RCC_APB2ENR_AFIOEN;    // Тактируем порт А, тактируем линию альтернативных функций
    GPIOA->CRH&=~(GPIO_CRH_CNF9|GPIO_CRH_CNF10);            // Вычищаем конфигурацию портов A9 и A10
    GPIOA->CRH|= GPIO_CRH_CNF9_1|GPIO_CRH_CNF10_0;          // A9 в режим push-pull, A10 в режиме входа Hi-Z
    GPIOA->CRH|= GPIO_CRH_MODE9;                            // 10 мГц на вход
    GPIOA->CRH&= ~GPIO_CRH_MODE10;                          // A10 в режиме входа Hi-Z
    
    RCC->APB2ENR|=RCC_APB2ENR_USART1EN;                     // тактирование USART1
    USART1->BRR=0x1D4C;                                     // 9600 бод (читай datasheet, таблица 192, Programmed value перевести
                                                            // в HEX и убрать запятую, т.е. 468.75d = 1D4.C -> BRR = 1D4C)
    USART1->CR1=USART_CR1_UE|USART_CR1_RE|USART_CR1_TE;     // включить приёмник, передатчик, запуск UART
    USART1->CR1|=USART_CR1_RXNEIE;                          // включаем прерывания по приёму данных
}

void UART_TRANSMIT(uint8_t uart_data)
{
    while((USART1->SR&USART_SR_TC)==0);                     // ждём флага очистки регистра отправки 
    USART1->DR = uart_data;	                                // отправляем данные    
}

uint8_t UART_RECEIVE(void)
{
    while((USART1->SR & USART_SR_RXNE)==0);                 //ждём появление данных в буфере
    return (USART1->DR);
}

