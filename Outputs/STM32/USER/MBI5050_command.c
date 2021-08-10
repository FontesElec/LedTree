/*

Комплект команд LED драйвера MBI5050 под ядро Cortex M3
Автор: Чудаков Максим
email: fontes35@mail.ru


* драйвер переписан под возможность каскадного соединения нескольких чипов, смотри datasheet
* GPIO функция реализована под чип stm32f103c8t6 используются порты:
    - B12 на вход SDI драйвера;
    - B13 и B14 как тактирующие выводы (можно ограничиться одним из них если подключить к выводу DCLK драйвера и прописать в 
    конфигурации использовать DCLK как источник тактирования для GCLK);
    - B15 на вход LE. 
В случае использования другого чипа, или других портов переписать define-ы макросов DCLK, LE и SDI в файле MBI5050_comand.h 
и переписать функцию MBI5050_GPIO_INIT в файле MBI5050_comand.c.

* CONF_REG_DATA является параметром конфигурации, где:
    - биты 15 и 14: всегда 0;
    - бит 13: 1 если GCLK тактируется от DCLK, 0 - если под GCLK выведена отдельная линия, f_gclk >= 20% f_dclk;
    - бит 12: 1 если 14 бит на канал (16384 значений PWM), 0 если 16 бит на канал (65536 значений PWM);
    - бит 11: всегда 0;
    - биты 10-8: количество рядов, от 1го при 0b000, до 8ми при 0b111, по умолчанию стоит 4 канала 0b011;
    - биты 7 и 6: всегда 0;
    - биты 5-0: цифровая подстройка значения тока открытого состояния портов, более подробно читай datasheet,
    дефолтное значение: 0b101011;

* Для использования функций  MBI5050_WRITE_CONF, MBI5050_ALL_OUT_EN и MBI5050_ALL_OUT_DIS нужно предварительно подать
команду MBI5050_PRE_ACTIVE;

* Для работы с памятью драйвера достаточно тактировать линию DCLK, в случае, если нужно запустить работу выходов OUT0-OUT15,
дополнительно тактируй линию GCLK или поменяй бит 13 регистра конфигурации (см. выше) вызовом команды MBI5050_WRITE_CONF c 
необходимым значением регистра на входе функции;

* Параметры ШИМ для каждого канала отправляются последовательно с помощью команды MBI5050_SEND_DATA, число команд равно числу каналов,
помноженных на 16, приём данных начинается с линии 0 канала 16, по завершении отправки данных нужно отправить команду V_SYNC;

* Порядок инициализации: 
1. Запустили функцию инициализации MBI5050_GPIO_INIT предварительно настроив её если нужны другие порты или используется другой чип;
2. Запустили функцию MBI5050_SOFT_RST сбросить настройки к дефолтным и остановить работу драйвера;
3. Задали нужные параметры регистра конфигурации если это необходимо с помощью MBI5050_WRITE_CONF, не забыть предварительно
запустить функцию MBI5050_PRE_ACTIVE;
4. Отправили все параметры каналов с помощью MBI5050_SEND_DATA;
5. Обновили отображение данных с помощью команды MBI5050_V_SYNC;

* В случае использования одного драйвера, в командах MBI5050_SEND_DATA и MBI5050_WRITE_CONF, второй параметр задавать как LE_ACK, 
в этом случае данные с шины уйдут в память драйвера. В случае использования нескольких драйверов, соединённых каскадом, необходимо 
задавать последовательно команды "MBI5050_SEND_DATA" или "MBI5050_WRITE_CONF" друг за другом столько раз, 
сколько драйверов задействовано, НО!!!!, все функции, кроме последней написанной в последовательности, должны иметь второй параметр
"LE_NACK", и только последняя функция этой последовательности - "LE_ACK", только в этом случае данные по цепи SDI-SDO передадутся 
до крайноего драйвера, а финальная функция с параметром "LE_ACK" даст команду одновременной загрузки данных
или параметров конфигурации, которые на данный момент лежат в сдвиговых регистрах чипов.

* При работе с несколькими чипами в каскадном соединении:
- MBI5050_SOFT_RST: достаточно задать один раз для сброса всех драйверов;
- MBI5050_WRITE_CONF: сначала единожды задаётся "MBI5050_PRE_ACTIVE", затем отправляется "MBI5050_WRITE_CONF" с только раз, сколько 
планируется задействовать чипов, второй параметр функции LE_ACK или LE_NACK используется по правилам, изложенным выше;
- MBI5050_ALL_OUT_EN: сначала единожды задаётся "MBI5050_PRE_ACTIVE", затем единожды "MBI5050_ALL_OUT_EN", включаются все чипы сразу;
- MBI5050_ALL_OUT_DIS: сначала единожды задаётся "MBI5050_PRE_ACTIVE", затем единожды "MBI5050_ALL_OUT_DIS", 
выключаются все чипы сразу;
- MBI5050_SEND_DATA: отправляется последовательно столько раз, сколько задействуется драйверов, второй параметр функции LE_ACK или 
LE_NACK используется по правилам, изложенным выше. Одна последовательность из MBI5050_SEND_DATA, записывает информацию лишь для одного
канала каждого из драйверов, соответственно, для полной загрузки всех драйверов, отправь последовательность MBI5050_SEND_DATA 16ть раз;
- MBI5050_V_SYNC: обновляет инофрмацию на драйверах и выводит полученные данные, запускается после полной загрузки данных во все 
нужные каналы всех драйверов.

!!! РАБОТА ДРАЙВЕРОВ С !!!ПАРАЛЕЛЬНОЙ ШИНОЙ!!!! (НЕ КАСКАДНОЙ) SDI И ВЫДЕЛЕННЫМИ КАНАЛАМИ LE НЕСТАБИЛЬНА, В ПРОЦЕССЕ 
КАНАЛЫ ДРАЙВЕРОВ ГАСНУТ ЧТО СОЗДАЁТ НЕНУЖНЫЙ ЭФФЕКТ МЕРЦАНИЯ, СХЕМА НЕ РАБОЧАЯ ПРИ ИСПОЛЬЗОВАНИИ БОЛЬШОГО ЧИСЛА ДРАЙВЕРОВ 
И/ИЛИ НЕОБХОДИМОСТИ ДИНАМИЧЕСКОГО ОБНОВЛЕНИЯ ДАННЫХ.
*/


#include "stm32f10x.h"                                              // Device header




void MBI5050_GPIO_INIT(void)
{
    RCC->APB2ENR|=RCC_APB2ENR_IOPBEN;                                                       //подаём тактирование на порт B
    GPIOB->CRH&=~(GPIO_CRH_CNF12|GPIO_CRH_CNF13|GPIO_CRH_CNF14|GPIO_CRH_CNF15);
    GPIOB->CRH|=GPIO_CRH_MODE12_1|GPIO_CRH_MODE13_1|GPIO_CRH_MODE14_1|GPIO_CRH_MODE15_1;
    
}

void MBI5050_SOFT_RST(void)
{
  //RESET

    for (inner_counter=0;inner_counter<=15;inner_counter++)
    {
        DCLK_H;
        __NOP;
        DCLK_L;
        if(inner_counter==0)
        {LE_H;}
        if(inner_counter==10)
        {LE_L;}
        
    }    
}

void MBI5050_PRE_ACTIVE(void)
{
    //PRE_ACTIVE

    for (inner_counter=0;inner_counter<=15;inner_counter++)
    {
        DCLK_H;
        __NOP;
        DCLK_L;
        if(inner_counter==0)
        {LE_H;}
        if(inner_counter==14)
        {LE_L;}
    }
}

void MBI5050_WRITE_CONF(uint16_t param, uint8_t data_ack)
{
    //WRITE_CONF

    for (inner_counter=0;inner_counter<=15;inner_counter++)
    {       
            if(((param>>(15-inner_counter))&1)==1)
            {
                SDI_H;
            }
            else
            {
                SDI_L;
            } 
            DCLK_H;
            __NOP;
            DCLK_L;
            if ((inner_counter==11)&data_ack)
            {
                LE_H;
            }
            if ((inner_counter==15)&data_ack)
            {
                LE_L;
            }
         
    } 
}

void MBI5050_ALL_OUT_EN(void)
{
     //ALL_EN

    for (inner_counter=0;inner_counter<=15;inner_counter++)
    {
        DCLK_H;
        __NOP;
        DCLK_L;
        if(inner_counter==0)
        {LE_H;}
        if(inner_counter==11)
        {LE_L;}
        
    }   
}

void MBI5050_ALL_OUT_DIS(void)
{
    //ALL_DIS

    for (inner_counter=0;inner_counter<=15;inner_counter++)
    {
        DCLK_H;
        __NOP;
        DCLK_L;
        if(inner_counter==0)
        {LE_H;}
        if(inner_counter==12)
        {LE_L;}
       
    }
}

void MBI5050_SEND_DATA(uint16_t data,uint8_t data_ack)
{
    //SEND DATA
        
    for (inner_counter=0;inner_counter<=15;inner_counter++)
    {       
            if(((data>>(15-inner_counter))&1)==1)
            {
                SDI_H;
            }
            else
            {
                SDI_L;
            } 
            DCLK_H;
            __NOP;
            DCLK_L;
            if ((inner_counter==14)&data_ack)
            {
                LE_H;
            }
            if ((inner_counter==15)&data_ack)
            {
                LE_L;
            }
         
    } 
    
    

}

void MBI5050_V_SYNC(void)
{
    //V_SYNC
    
    
    for (inner_counter=0;inner_counter<=55;inner_counter++)
    {
        DCLK_H;
        __NOP;
        DCLK_L;
        if(inner_counter==53)
        {LE_H;}
        
        
    } 
    
    DCLK_H;
    __NOP;
    DCLK_L;
    LE_L;
    for(int t=10;t>0;t--);
    
}
