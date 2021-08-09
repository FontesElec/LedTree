#include "stm32f10x.h"                                              // Device header


//ФУНКЦИИ РАБОТЫ С ЦВЕТОМ

void SET_BRIGHTNESS(void);                                          // принять значение яркости
void COLOR_RECEIVE(void);                                           // получить параметр цвета
void GET_DISPERSION(void);                                          // получить параметр разброса цветов между листами
void SET_CH_SPEED(void);                                            // задать скорость изменения параметра
void FILL_MATRIX_MONO(uint8_t,uint8_t,uint8_t);                     // заполнить матрицу монотонным значением цвета
void ADD_DISPERSION(uint8_t,uint8_t,uint8_t);                       // внести дисперсию в каналы
void DATA_UPDATE(void);                                             // выводим и обновляем данные
void GO_TO_COLOR(uint8_t,uint16_t);                                 // система перехода к заданному цвету
void DELAY(uint32_t);                                               // задержка времени


//==================================================================================================================
