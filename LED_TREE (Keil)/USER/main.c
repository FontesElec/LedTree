/*
Проект RGB дерева с управлением по блютуз
Автор: Чудаков Максим
email: fontes35@mail.ru

* Прошивка под чип STM32F103C8T6
* Основные настройки конфигурации работы платы лежат в файле main.h
* Проект расчитан на управление драйверами светодиодов MBI5050GP, соединение шины данных последовательное, 
базовая программа управления драйверами лежит в файлах MBI5050_command.c и MBI5050_command.h
* МК принимает данные по UART1, соответствующие настройки работы лежат в файле stm32f10x_uart.c и stm32f10x_uart.h 

* Основная часть программы вертится вокруг цикличной прокрутки тем в модуле выбора режимов.
* Изменение и приём данных происходит в теле цикла прерывания по UART
* Базовые функции работы подсветки лежат в файле lighting functions.c и lighting functions.h
* Работа самих тем подсветки прописана в файлах themes.c и themes.h

*/



#include <stdlib.h>
#include "stm32f10x.h"                                              // Device header
#include "main.h"
#include "MBI5050_command.h"                                        // Макросы и описание команд драйвера 
#include "MBI5050_command.c"                                        // Реализация команд драйвера
#include "stm32f10x_uart.h"                                         // Описание функций UART
#include "stm32f10x_uart.c"                                         // Реализация функций UART
#include "lighting functions.h"                                     // Определители функций управления подсветкой
#include "lighting functions.c"                                     // Реализация функций управления подсветкой
#include "themes.h"                                                 // Описание тем подсветки
#include "themes.c"                                                 // Реализация тем подсветки

//==================================================================================================================

uint8_t mode=DEFAULT_MODE;                                          // выбранный режим подсветки
uint8_t sub_mode=DEFAULT_SUB_MODE;                                  // с его помощью будем переключаться между подрежимами тем
uint8_t data_changed=0;                                             // сигнал об изменении данных в режиме
uint8_t uart_data=0;                                                // данные, полученные с UART
uint16_t counter=0;                                                 // просто счётчик


int main(void)
{
    //Сброс и старотовая настройка оборудования
    
    UART_INIT();
    MBI5050_GPIO_INIT();   
    MBI5050_SOFT_RST();
    MBI5050_PRE_ACTIVE();
    for(counter=NUM_OF_DRIVERS;counter>0;counter--)                 // настраивапем режим работы драйверов
    {
        if(counter==1)
        {
            MBI5050_WRITE_CONF(CONF_REG_DATA,LE_ACK);
        }
        else
        {
            MBI5050_WRITE_CONF(CONF_REG_DATA,LE_NACK);
        }
    }
    
   NVIC_EnableIRQ(USART1_IRQn);                                      // разрешаем прирывания по UART1
   
   /*
   ---------------------БЛОК ЦИКЛИЧЕСКОЙ ПРОКРУТКИ ВЫБОРА ТЕМ------------------
    
   В СЛУЧАЕ ДОБАВЛЕНИЯ НОВОЙ ФУНКЦИИ ПОДСВЕТКИ, ДОБАВИТЬ ЕЁ В АРГУМЕНТЫ SWITCH ПОД НОВЫМ НОМЕРОМ
   далее необходимо вписать новую функцию в дерево выбора режимов, расположенного в функции обработки прерывания по UART
    
   ==============================================================================
    */
    
    
   //-----------------------------ВЫБОР РЕЖИМА---------------------------------- 
    while(1)
    {    
       switch(mode)
       {
           case 0:  START_MODE();            break;         //Старотвоый режим
           case 1:  BT_CONN_COMPLETE();      break;         //Сигнал об успешном подключении Bluetooth
           case 2:  MANUAL_COLOR();          break;         //Режим ручной настройки цвета
           case 3:  SEASONS();               break;
           case 4:  FIRE_STYLES();           break;
           case 5:  GRADIENTS();             break;
           case 6:  SNAKES();                break;
           case 7:  FIREWORK();              break;
           case 8:  RANDOM_COLOR();          break;
           default: START_MODE();
       }

    }
    
    //===========================================================================
}

void USART1_IRQHandler(void)
{
    //ЭТОТ БЛОК ОТВЕЧАЕТ ЗА РЕЖИМЫ РАБОТЫ В ЗАВИСИМОСТИ ОТ ПРИНЯТЫХ ПАРАМЕТРОВ
    //фактически это просто программная имитация менюшки
    
    USART1->CR1&=~USART_CR1_RXNEIE;                          // выключаем прерывания по приёму данных
    uart_data=UART_RECEIVE();
    switch (uart_data)
    {
        case 83:                             //"S", по протоколу этот параметр в принципе не должен стоять в начале передачи  
                                             //кроме случаев сбоя питания или связи
        
            mode=0; break;                   //но если произошло, то переходим в стартовый режим 
        
        case 67:                             //"С" в ASCII, переключаем индикацию на мигающий жёлтый, блютуз подключён
        {
          mode=1;
          data_changed=1;
          uart_data=UART_RECEIVE();                          // ожидаем "S" как сигнал об окончании передачи                            
          if(uart_data!=83)                                  // если приходит иное, то сбрасываем режим на стартовый                                   
          {mode=0;}
          
          break;
        }
        
        case 77:                                             //"M", режим мануального переключения цвета
        {
            mode=2;                                          //меняем параметр режима
            uart_data=UART_RECEIVE();
            switch(uart_data)
            {
                case 66:                                     //"B" в ASCII, меняем яркость всех светодиодов 
                {    
                    SET_BRIGHTNESS();   break;
                }
                case 67:                                     //"С" в ASCII, меняем базовый цвет всех светодиодов
                {
                    COLOR_RECEIVE();    break;
                }
                case 68:                                     // "D" в ASCII, задаём разброс между светодиодами
                {
                    GET_DISPERSION();   break;
                }
                case 83:                                     // "S" в ASCII, возврат к основному режиму
                {
                    mode=0;             break;
                }
                default: mode=0;
            }
            
            break;                     
        }
        
        case 84:                                             //"T", переход в меню тем
        {
            uart_data=UART_RECEIVE();
            switch(uart_data)
            {
                 case 65:                                    // "A" в ASCII, тема времён года
                 {
                        mode=3;
                        uart_data=UART_RECEIVE();
                        switch(uart_data)
                        {
                            case 65:                         // "A" в ASCII, лето
                            {    
                                sub_mode=0;
                                data_changed=1;
                                uart_data=UART_RECEIVE();    // ожидаем "S" как сигнал об окончании передачи
                                if(uart_data!=83)            // если приходит иное, то сбрасываем режим на стартовый                                   
                                {mode=0;}
                                break;
                                
                            }
                            
                            case 69:                         // "E" в ASCII, осень
                            {    
                                sub_mode=1; 
                                data_changed=1;
                                uart_data=UART_RECEIVE();
                                break;
                            }
                            case 70:                         // "F" в ASCII, зима
                            {    
                                sub_mode=2;
                                data_changed=1;
                                uart_data=UART_RECEIVE();    // ожидаем "S" как сигнал об окончании передачи                            
                                if(uart_data!=83)            // если приходит иное, то сбрасываем режим на стартовый                                   
                                {mode=0;}
                                break;                                
                            }
                            case 71:                         // "G" в ASCII, весна
                            {    
                                sub_mode=3;
                                data_changed=1;
                                uart_data=UART_RECEIVE();    // ожидаем "S" как сигнал об окончании передачи                            
                                if(uart_data!=83)            // если приходит иное, то сбрасываем режим на стартовый                                   
                                {mode=0;}
                                break;
                            }
                            case 66:                         // "B" в ASCII, меняем яркость всех светодиодов 
                            {    
                                SET_BRIGHTNESS();   break;
                            }
                            case 67:                         // "С" в ASCII, меняем цвет
                            {
                                COLOR_RECEIVE();    break;
                            }
                    
                            case 83:                         // "S" в ASCII, возврат к основному режиму
                            {
                                mode=0;             break;   // если параметр не соответствует протоколу, сбрасываемся
                            }
                            
                            default: mode=0;
                        }                     
                        break;                        
                 }
                 case 66:                                    // "B" в ASCII, темы имитации огня
                 {
                         mode=4;
                         data_changed=1;
                         uart_data=UART_RECEIVE();
                         switch(uart_data)
                         {
                             case 65:                        // "A" в ASCII, свеча
                            {    
                                sub_mode=0;
                                uart_data=UART_RECEIVE();    // ожидаем "S" как сигнал об окончании передачи                            
                                if(uart_data!=83)            // если приходит иное, то сбрасываем режим на стартовый                                   
                                {mode=0;}
                                break;                                
                            }                            
                             case 69:                        // "E" в ASCII, угольки
                            {    
                                sub_mode=1;
                                uart_data=UART_RECEIVE();    // ожидаем "S" как сигнал об окончании передачи                
                                if(uart_data!=83)            // если приходит иное, то сбрасываем режим на стартовый                                   
                                {mode=0;}
                                break;         
                            }
                            case 70:                         // "F" в ASCII, пламя
                            {    
                                sub_mode=2;
                                uart_data=UART_RECEIVE();    // ожидаем "S" как сигнал об окончании передачи                            
                                if(uart_data!=83)            // если приходит иное, то сбрасываем режим на стартовый                                   
                                {mode=0;}
                                break;                                
                            }        
                            case 66:                         // "B" в ASCII, меняем яркость всех светодиодов 
                            {    
                                SET_BRIGHTNESS();   break;
                            }
                            case 83:                         // "S" в ASCII, возврат к основному режиму
                            {
                                mode=0;             break;   
                            }
                            default: mode=0;                 // если параметр не соответствует протоколу, сбрасываемся
                        }
                        break;
                 }
                 
                 case 67:                                    // "C" в ASCII, режим градиентов
                 {
                        mode=5;
                        uart_data=UART_RECEIVE();
                        switch(uart_data)
                        {
                            case 65:                        // "A" в ASCII, градиент по кругу
                            {    
                                sub_mode=0;
                                uart_data=UART_RECEIVE();    // ожидаем "S" как сигнал об окончании передачи                            
                                if(uart_data!=83)            // если приходит иное, то сбрасываем режим на стартовый                                   
                                {mode=0;}
                                break;                                
                            }                            
                             case 69:                        // "E" в ASCII, градиент по рандомным цветам
                            {    
                                sub_mode=1;
                                uart_data=UART_RECEIVE();    // ожидаем "S" как сигнал об окончании передачи                
                                if(uart_data!=83)            // если приходит иное, то сбрасываем режим на стартовый                                   
                                {mode=0;}
                                break;         
                            }
                            case 70:                         // "F" в ASCII, радуга
                            {    
                                sub_mode=2;
                                data_changed=1;
                                ch_speed=5;
                                uart_data=UART_RECEIVE();    // ожидаем "S" как сигнал об окончании передачи                            
                                if(uart_data!=83)            // если приходит иное, то сбрасываем режим на стартовый                                   
                                {mode=0;}
                                break;                                
                            }    
                            case 66:                         // "B" в ASCII, меняем яркость всех светодиодов 
                            {    
                                SET_BRIGHTNESS();   break;
                            }
                            case 86:                         // "V" в ASCII, меняем скорость изменения параметров подсветки
                            {    
                                SET_CH_SPEED();   break;
                            }
                            case 83:                         // "S" в ASCII, возврат к основному режиму
                            {
                                mode=0;             break;   // если параметр не соответствует протоколу, сбрасываемся
                            }
                            default: mode=0;                            
                            
                        }                            
                        break;
                 }
                 case 68:                                    // "D" в ASCII, "змейки"
                 {
                        mode=6;
                        uart_data=UART_RECEIVE();
                        switch(uart_data)
                        {
                            case 65:                         // "A" в ASCII, одна "змейка" за период
                            {    
                                sub_mode=0;
                                uart_data=UART_RECEIVE();    // ожидаем "S" как сигнал об окончании передачи                            
                                if(uart_data!=83)            // если приходит иное, то сбрасываем режим на стартовый                                   
                                {mode=0;}
                                break;                                
                            }                            
                            case 69:                        // "E" в ASCII, две "змейки" за период
                            {    
                                sub_mode=1;
                                uart_data=UART_RECEIVE();    // ожидаем "S" как сигнал об окончании передачи                
                                if(uart_data!=83)            // если приходит иное, то сбрасываем режим на стартовый                                   
                                {mode=0;}
                                break;         
                            }
                            case 66:                         // "B" в ASCII, меняем яркость всех светодиодов 
                            {    
                                SET_BRIGHTNESS();   break;
                            }
                             case 67:                         // "С" в ASCII, меняем цвет
                            {
                                COLOR_RECEIVE();    break;
                            }
                            case 83:                         // "S" в ASCII, возврат к основному режиму
                            {
                                mode=0;             break;   // если параметр не соответствует протоколу, сбрасываемся
                            }
                            default: mode=0;                            
                            
                        }                            
                        break;
                 }
                 case 69:                                    // "E" в ASCII, салют
                 {
                        mode=7;
                        uart_data=UART_RECEIVE();
                        switch(uart_data)
                        {
                            
                            case 66:                         // "B" в ASCII, меняем яркость всех светодиодов 
                            {    
                                SET_BRIGHTNESS();   break;
                            }
                            case 67:                         // "С" в ASCII, меняем цвет
                            {
                                COLOR_RECEIVE();    break;
                            }
                            case 83:                         // "S" в ASCII, возврат к основному режиму
                            {
                                mode=0;             break;   // если параметр не соответствует протоколу, сбрасываемся
                            }
                            default: mode=0;                            
                            
                        }                            
                        break;
                 }
                 
                 case 70:                                    // "F" в ASCII, выспышки случайного цвета
                 {
                        mode=8;
                        uart_data=UART_RECEIVE();
                        switch(uart_data)
                        {
                            
                            case 66:                         // "B" в ASCII, меняем яркость всех светодиодов 
                            {    
                                SET_BRIGHTNESS();   break;
                            }
                            case 83:                         // "S" в ASCII, возврат к основному режиму
                            {
                                mode=0;             break;   // если параметр не соответствует протоколу, сбрасываемся
                            }
                            default: mode=0;                            
                            
                        }                            
                        break;
                 }
                 
                 
                 case 83:                                    // "S" в ASCII, возврат к основному режиму
                 {
                        mode=0;             break;           // если параметр не соответствует протоколу, сбрасываемся
                 }
                 default: mode=0;                
              }            
            break; 
        }
        default: mode=0;                                     // если параметр не соответствует протоколу, сбрасываемся
    }
        
    USART1->CR1|=USART_CR1_RXNEIE;                           // включаем прерывания по приёму данных    
}

