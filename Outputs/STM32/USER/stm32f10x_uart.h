/*

Комплект команд UART под ядро Cortex M3


* настройка на скорость 9600 бод
* чип STM32F103C8T6, настройка UART1 A9,A10

*/
#include "stm32f10x.h"                                   // Device header

void UART_INIT(void);                                    // Инициализация портов и настройка uart       
void UART_TRANSMIT(uint8_t uart_data);                   // Передать байт данных
uint8_t UART_RECEIVE(void);                              // Принять байт данных
