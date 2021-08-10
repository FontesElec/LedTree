/*
Проект RGB дерева с управлением по блютуз

Автор: Чудаков Максим
email: fontes35@mail.ru


*/


#include "stm32f10x.h"                                              // Device header
#include "main.h"
#include "lighting functions.h"


uint16_t color[3];                                                  // базовый цвет
uint16_t brightness=128;                                            // значение шим для текущего канала
uint8_t dispersion[3];                                              // значения разброса параметров цвета
uint16_t ch_speed=0;                                                // скорость изменения параметра

uint16_t channel_data[3][OUTPUTS_PER_CH];                           // цветовая матрица
uint16_t n_data=0;                                                  // номер параметра в цветовой матрице
uint8_t n_channel=0;                                                // номер цветового канала
uint8_t n_output=0;                                                 // номер вывода драйвера




extern uint8_t mode;                                                // выбранный режим подсветки
extern uint8_t sub_mode;                                            // с его помощью будем переключаться между подрежимами тем
extern uint8_t data_changed;                                        // сигнал об изменении данных в режиме
extern uint8_t uart_data;                                           // данные, полученные с UART



//----------------------------------------------БЛОКИ РАБОТЫ С ЦВЕТОМ---------------------------------------------

void DATA_UPDATE(void)
{
   
    //ВЫВОДИМ ДАННЫЕ НА ДРАЙВЕРА  

    /*
    ПОСЛЕДОВАТЕЛЬНОСТЬ ВЫВОДА ДАННЫХ: 
    
    -    начинаем с вывода на все 16е каналы всех драйверов, начиная с последнего, то есть
    синий канал,все 16е выводы драйверов, зелёный канал, все 16е выводы, красный канал, все 16е выводы с подтверждением (ACK)
    о завершении пакета данных на последнем драйвере.
    
    -    затем все 15е каналы всех драйверов
    
    -    после того, как передадим последний байт на 1й канал 1го драйвера, вызываем функцию V_SYNC
    
    */
    
    for(n_output=16;n_output>0;n_output--)
    {
        for(n_channel=3;n_channel>0;n_channel--)
        {
            for(n_data=DRIVERS_PER_CH;n_data>0;n_data--)
            {
                
                if((n_channel==1)&(n_data==1))
                {
                    MBI5050_SEND_DATA(channel_data[n_channel-1][((n_data-1)*16+(n_output-1))]*brightness,LE_ACK);
                }
                else
                {
                    MBI5050_SEND_DATA(channel_data[n_channel-1][((n_data-1)*16+(n_output-1))]*brightness,LE_NACK);
                }
            }
        }        
    }
    
    MBI5050_V_SYNC();
    
    
 
}

void DELAY(uint32_t ch_speed)
{
    for(ch_speed;ch_speed>0;ch_speed--)
    {
        DCLK_H;
        DCLK_L;
    }
}

void COLOR_RECEIVE(void)                                                                
{
    data_changed=1;                                          // взводим флаг изменённых данных
    for(n_channel=0;n_channel<3;n_channel++)
    {
        color[n_channel]=UART_RECEIVE();                     // получаем данные по 3м каналам цвета
    }
    uart_data=UART_RECEIVE();                                // ожидаем "S" как сигнал об окончании передачи                            
    if(uart_data!=83)                                        // если приходит иное, то сбрасываем режим на стартовый                                   
    {mode=0;}
}

void SET_BRIGHTNESS(void)                                                               
{
    data_changed=1;                                          // взводим флаг изменённых данных
    brightness=UART_RECEIVE();                               // получаем данные по необходимой яркости
    uart_data=UART_RECEIVE();                                // ожидаем "S" как сигнал об окончании передачи                            
    if(uart_data!=83)                                        // если приходит иное, то сбрасываем режим на стартовый                                   
    {mode=0;}       
}


void GET_DISPERSION(void)
{
    data_changed=1;                                          // взводим флаг изменённых данных
    for(n_channel=0;n_channel<3;n_channel++)
    {
        dispersion[n_channel]=UART_RECEIVE();                // получаем данные по разбросу цвета
    }
     uart_data=UART_RECEIVE();                               // ожидаем "S" как сигнал об окончании передачи                            
    if(uart_data!=83)                                        // если приходит иное, то сбрасываем режим на стартовый                                   
    {mode=0;}
}

void SET_CH_SPEED(void)
{
    data_changed=1;                                          // взводим флаг изменённых данных
    ch_speed=UART_RECEIVE();                                 // получаем данные скорости изменения
    uart_data=UART_RECEIVE();                                // ожидаем "S" как сигнал об окончании передачи                            
    if(uart_data!=83)                                        // если приходит иное, то сбрасываем режим на стартовый                                   
    {mode=0;}
}

void FILL_MATRIX_MONO(uint8_t color_0,uint8_t color_1,uint8_t color_2)
{
    
        for(n_data=0;n_data<OUTPUTS_PER_CH;n_data++)
        {
            channel_data[0][n_data]=color_0;            
        }  
        for(n_data=0;n_data<OUTPUTS_PER_CH;n_data++)
        {
            channel_data[1][n_data]=color_1;            
        }
        for(n_data=0;n_data<OUTPUTS_PER_CH;n_data++)
        {
            channel_data[2][n_data]=color_2;            
        }          
        
}



void ADD_DISPERSION(uint8_t r_disp, uint8_t g_disp, uint8_t b_disp)
{
    //ДОБАВЛЯЕМ РАЗБРОС В ОСНОВНЫЕ ЗНАЧЕНИЯ ЦВЕТА
    
    for(n_channel=0;n_channel<3;n_channel++)
    {
        for(n_data=0;n_data<OUTPUTS_PER_CH;n_data++)
        {
            uint16_t random_num=((rand()/8421505));
            
            channel_data[n_channel][n_data]=channel_data[n_channel][n_data]+(random_num-channel_data[n_channel][n_data])*dispersion[n_channel]/100;
            
        }
    }
}


void GO_TO_COLOR(uint8_t num_current_color, uint16_t chg_speed )
{
    
    uint16_t current_color[6][3]  = {{511,0,0},         //создаём стандартные модели цветов, красный
                                    {255,255,0},        //жёлтый
                                    {0,511,0},          //зелёный
                                    {0,255,255},        //голубой
                                    {0,0,511},          //синий
                                    {255,0,255}};       //фиолетовый
   
    
     while((color[0]!=current_color[num_current_color][0])|
            (color[1]!=current_color[num_current_color][1])|
            (color[2]!=current_color[num_current_color][2]))     //в процессе будем пытаться достигнуть заданных параметров цвета   
        {
            for(n_channel=0;n_channel<3;n_channel++)
            {
                if(color[n_channel]>(current_color[num_current_color][n_channel]))
                {color[n_channel]--;}
                if(color[n_channel]<(current_color[num_current_color][n_channel]))
                {color[n_channel]++;}
            }
           
                           
            for(int i=0;i<OUTPUTS_PER_CH;i++)
            {
               channel_data[0][i]=color[0];
               channel_data[1][i]=color[1];
               channel_data[2][i]=color[2];
            }         
            DATA_UPDATE();  
            
            
            for(int i=chg_speed+1;i>0;i--)
            {
                for(int j=chg_speed+1;j>0;j--)
                {
                    
                    if(data_changed==1)                     //создаём условие для сквозного просокока в случае смены данных
                    {
                        color[0]=current_color[num_current_color][0];
                        color[1]=current_color[num_current_color][1];
                        color[2]=current_color[num_current_color][2];
                        i=1;
                        j=1;
                        
                    }
                    DCLK_H;
                    DCLK_L;
                }
            }
        
    
        }
}

//======================================================================================================================

