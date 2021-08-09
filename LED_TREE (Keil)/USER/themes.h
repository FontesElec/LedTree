#include "stm32f10x.h"                                              // Device header

//РЕЖИМЫ РАБОТЫ ПОДСВЕТКИ
//ВНИМАНИЕ! ПРИ ДОБАВЛЕНИИ НОВОЙ ФУНКЦИИ ОБЯЗАТЕЛЬНО ВНЕСТИ ЕЁ ИНИЦИАЛИЗАЦИЮ В ФУНКЦИЮ switch в файле main.c 

void START_MODE(void);                                              // стартовый режим работы устройства
void BT_CONN_COMPLETE(void);                                        // режим успешного подключения по bluetooth
void MANUAL_COLOR(void);                                            // режим самостоятельного выбора цвета
void SEASONS(void);                                                 // времена года
void FIRE_STYLES(void);                                             // режим тем огня
void GRADIENTS(void);                                               // переливы
void SNAKES(void);                                                  // режим бегущих огней
void FIREWORK(void);                                                // имитация салюта
void RANDOM_COLOR(void);                                            // режим случайного выбора цвета
