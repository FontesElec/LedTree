/*
Проект RGB дерева с управлением по блютуз
Файл реализации работы тем подсветки

Автор: Чудаков Максим
email: fontes35@mail.ru



*/

#include "stm32f10x.h"                                              // Device header
#include "main.h"

extern uint16_t counter;
extern uint8_t n_channel;




//-----------------------------------------БЛОКИ ОПИСАНИЯ ФУНКЦИЙ ПОДСВЕТКИ---------------------------------------------

void START_MODE(void)
{
    /*
    если включается этот режим, значит только что произошёл сбой по питанию или передаче данных, 
    дерево будет плавно пульсировать красным
    */   
    
    //КРАСНЫЙ ЦВЕТ   
    
    color[0]=255;
    color[1]=0;
    color[2]=0;
    
    FILL_MATRIX_MONO(color[0],color[1],color[2]);        // загружаем в драйверы красный цвет
    
    //ЗАПУСКАЕМ МИГАЛКУ
    
    while(mode==0)
    {        
        for (brightness=0;brightness<128;brightness++)
        {
            DATA_UPDATE();
            DELAY(10000);
            if(data_changed==1)                          // условие сквозного проскока цикла если сменён режим
            {
                brightness=127;
            }
                                                 
        }
        
        for (brightness=127;brightness>0;brightness--)
        {
           
            DATA_UPDATE();
            DELAY(10000);
            if(data_changed==1)                          // условие сквозного проскока цикла если сменён режим
            {
                brightness=1;
                data_changed=0;
            }                           
        }             
    }
}



void BT_CONN_COMPLETE(void)
{
    /*
    если включается этот режим, значит только что произошло подключение по bluetooth, 
    дерево будет плавно пульсировать жёлтым
    */  

    //ЖЁЛТЫЙ ЦВЕТ
    
    color[0]=255;
    color[1]=255;
    color[2]=0;
    
    FILL_MATRIX_MONO(color[0],color[1],color[2]);        // загружаем жёлтый цвет в драйверы
    
    while(mode==1)
    {        
        for (brightness=0;brightness<128;brightness++)
        {
            DATA_UPDATE();
            DELAY(10000);
            if(data_changed==1)                          // условие сквозного проскока цикла если сменён режим
            {
                brightness=127;
            }
                                                 
        }
        
        for (brightness=127;brightness>0;brightness--)
        {
           
            DATA_UPDATE();
            DELAY(10000);
            if(data_changed==1)                          // условие сквозного проскока цикла если сменён режим
            {
                brightness=1;
                data_changed=0;
            }                           
        }             
    }
}

void MANUAL_COLOR(void)
{
//----- Стартовые настройки цвета -----  
    
    brightness=128;
    dispersion[0]=0;  
    dispersion[1]=0;
    dispersion[2]=0;
   
    
//=====================================    
    
    //в этом режиме происходит простое ручное управление цветом, меняем яркость, цвет и дисперсию цвета между листами
    
    FILL_MATRIX_MONO(color[0],color[1],color[2]);
    ADD_DISPERSION(0,0,0);
    DATA_UPDATE();
    
    while(mode==2)
    {
        DCLK_H;
        DCLK_L;
        if(data_changed==1)                                       // при изменении данных перезаливаем информацию в драйвера
        {
            FILL_MATRIX_MONO(color[0],color[1],color[2]);
            ADD_DISPERSION(dispersion[0],dispersion[1],dispersion[2]);
            DATA_UPDATE();
            data_changed=0;
        }
    }
}



void SEASONS(void)                                                 // времена года
{
  
    //в зависимости от выбранного режима меняется тип подсветки, соответствующей выбранному времени года
    
  while(mode==3)
  {      
    //ЛЕТО
    if((mode==3)&(sub_mode==0))
    {
       
        // Зелёная подсветка с незначительными красным каналом и разбросом по цвету
        
        color[0]=60;
        color[1]=255;   
        color[2]=0;
        brightness=100;
        dispersion[0]=50;  
        dispersion[1]=50;
        dispersion[2]=0;
        
        FILL_MATRIX_MONO(color[0],color[1],color[2]);
        ADD_DISPERSION(dispersion[0],dispersion[1],dispersion[2]);
        DATA_UPDATE();
        while((mode==3)&(sub_mode==0))
        {
            DCLK_H;
            __NOP;
            DCLK_L;
            if((data_changed==1)&(mode==3))
            {
                FILL_MATRIX_MONO(color[0],color[1],color[2]);
                ADD_DISPERSION(dispersion[0],dispersion[1],dispersion[2]);
                DATA_UPDATE();
                data_changed=0;
            }
        }                   
    }
    
    //ОСЕНЬ
    else if((mode==3)&(sub_mode==1))
    {
        
        //Подсветка меняется с жёлто-зелёной, жёлто-красной, до красной 
        //Сам стиль будет расчитываться на устройстве управления и выдаваться просто как набор цветов
        color[0]=110;
        color[1]=130;   
        color[2]=0;
        brightness=60;
        dispersion[0]=30;  
        dispersion[1]=30;
        dispersion[2]=0;
        
        FILL_MATRIX_MONO(color[0],color[1],color[2]);
        ADD_DISPERSION(dispersion[0],dispersion[1],dispersion[2]);
        DATA_UPDATE();
        while((mode==3)&(sub_mode==1))
        {
            DCLK_H;
            __NOP;
            DCLK_L;
            if((data_changed==1)&(mode==3))
            {
                FILL_MATRIX_MONO(color[0],color[1],color[2]);
                ADD_DISPERSION(dispersion[0],dispersion[1],dispersion[2]);
                DATA_UPDATE();
                data_changed=0;
            }
        }       
    }
    
    //ЗИМА
    else if((mode==3)&(sub_mode==2))
    {
       
        //Подсветка бело-голубая
     
        color[0]=160;   
        color[1]=160;
        color[2]=200;
        brightness=64;
        dispersion[0]=0;  
        dispersion[1]=0;
        dispersion[2]=80;

        FILL_MATRIX_MONO(color[0],color[1],color[2]);
        ADD_DISPERSION(dispersion[0],dispersion[1],dispersion[2]);
        DATA_UPDATE();
        while((mode==3)&(sub_mode==2))
        {
            DCLK_H;
            __NOP;
            DCLK_L;
            if((data_changed==1)&(mode==3))
            {
                FILL_MATRIX_MONO(color[0],color[1],color[2]);
                ADD_DISPERSION(dispersion[0],dispersion[1],dispersion[2]);
                DATA_UPDATE();
                data_changed=0;
            }
        }       
    }
    
    //ВЕСНА    
    
    else if((mode==3)&(sub_mode==3))
    {
       
        //Шуточный режим, едва заметный зелёный на макисмальной яркости
        
        color[0]=0;   
        color[1]=1;
        color[2]=0;
        brightness=64;
        dispersion[0]=0;  
        dispersion[1]=0;
        dispersion[2]=0; 
        
        FILL_MATRIX_MONO(color[0],color[1],color[2]);
        ADD_DISPERSION(dispersion[0],dispersion[1],dispersion[2]);
        DATA_UPDATE();
        while((mode==3)&(sub_mode==3))
        {
             DCLK_H;
            __NOP;
            DCLK_L;
            if((data_changed==1)&(mode==3))
            {
                
                FILL_MATRIX_MONO(color[0],color[1],color[2]);
                ADD_DISPERSION(dispersion[0],dispersion[1],dispersion[2]);
                DATA_UPDATE();
                data_changed=0;
            }
        }       
     }
  }
}



void FIRE_STYLES(void)
{
    while(mode==4)
    {
        //СВЕЧА
        if((mode==4)&(sub_mode==0))
        {
            //----- Стартовые настройки цвета -----  
                
                color[2]=0;   
                color[1]=0;
                color[0]=0;
                brightness=128;
                
            //=====================================    
  
            while((mode==4)&(sub_mode==0))
            {
                uint16_t random_number=(rand()/8659208);    //уменьшаем до максимально возможного значения в 248
                for(int i=40;i>0;i--)
                {                                
                   color[0]=(color[0]+((random_number*3/5+80)-color[0])/40); 
                   color[1]=(color[1]+((random_number/2)-color[1])/40);
                  
                   FILL_MATRIX_MONO(color[0],color[1],color[2]);
            
                    
                   DATA_UPDATE();
                }
            }
        } 
        
        //ГОРЯЩИЕ УГЛИ
        
        else if((mode==4)&(sub_mode==1))
        {
            //----- Стартовые настройки цвета -----  
               
                brightness=64;
                uint8_t time_update=0;                      //период обновления плавающих значений яркости
                uint16_t random_number[OUTPUTS_PER_CH];     //матрица хранения плавающих значений яркости для каждого листа
                
            //=====================================    
            
            while((mode==4)&(sub_mode==1))
            {    
                if(time_update==0)
                {
                    for(int i=0;i<OUTPUTS_PER_CH;i++)
                    {    
                        random_number[i]=(rand()/4194304/120)*120;    //уменьшаем до максимально возможного значения в 512
                                                                      //деление на увеличенное число с умножением на 120 для большего разброса внутри предела
                    }
                    time_update=30;                                   //период обновления значений
                }
               for(int i=0;i<OUTPUTS_PER_CH;i++)
               {
                  channel_data[1][i]=(channel_data[1][i]+(((random_number[i]*random_number[i])/1024)-channel_data[1][i])/40);
                  channel_data[0][i]=(channel_data[1][i]*1.8+80);
                  channel_data[2][i]=0;
               }
               
               DATA_UPDATE();
               time_update--;              
            }            
        }
        
        //ИМИТАЦИЯ ОГНЯ
        
        else if((mode==4)&(sub_mode==2))
        {
            //----- Стартовые настройки цвета -----  
            
            uint16_t time_update=0;
            uint16_t random_number[OUTPUTS_PER_CH]; 
            brightness=64;
            
            //=====================================    
            while((mode==4)&(sub_mode==2))
            {   
                if(time_update==0)
                {
                    for(int i=0;i<OUTPUTS_PER_CH;i++)
                    {    
                        random_number[i]=(rand()/4194304/100)*100;    //уменьшаем до максимально возможного значения в 512
                                                                      //деление на увеличенное число с умножением на 100 для большего разброса внутри предела
                    }
                    
                    time_update=30;                                 //период обновления значений
                    
                }
                
                for(int i=0;i<OUTPUTS_PER_CH;i++)
                {
                   
                    channel_data[0][i]=(1+channel_data[0][i]+((random_number[i]/(i/10+1))-channel_data[0][i])/30);
                    channel_data[1][i]=(channel_data[1][i]+(((random_number[i]*random_number[i]*0.8)/512/(i*i/100+1))-channel_data[1][i])/30);
                    channel_data[2][i]=0;
                                    
                }
                
                DATA_UPDATE();
                time_update--;
            }                            
        }             
    }    
}

void GRADIENTS(void)
{
     uint8_t num_current_color=0;
     brightness=64;
     ch_speed=25;
    
        color[0]=510;
        color[1]=0;
        color[2]=0;
        
    
    while(mode==5)
    {
        
    //ГРАДИЕНТ ПО КРУГУ
        
       if((mode==5)&(sub_mode==0))
       {
           ch_speed=25; 
           
           while((mode==5)&(sub_mode==0))
            {
                GO_TO_COLOR(num_current_color, ch_speed*20);  
                num_current_color++;
                if(num_current_color>5)
                {num_current_color=0;}
                
                if(data_changed==1)
                {
                    GO_TO_COLOR(num_current_color,ch_speed*20);
                    data_changed=0;
                }
              
                
                
            }
           
       }
        
     //ГРАДИЕНТ ПО РАНДОМНЫМ ТОЧКАМ
        
        if((mode==5)&(sub_mode==1))
        {
            ch_speed=25;
            
            while((mode==5)&(sub_mode==1))
            {
                GO_TO_COLOR(num_current_color,ch_speed*20);
                uint8_t num_current_color_old=num_current_color;
            
                num_current_color=(rand()%6);
                
                if(num_current_color==num_current_color_old)
                {num_current_color++;}
                
                if(num_current_color>5)
                {num_current_color=0;}
                
                if(data_changed==1)
                {
                    GO_TO_COLOR(num_current_color,ch_speed*20);
                    data_changed=0;
                }
                
            }
        }
    

        //РАДУГА
        
        if((mode==5)&(sub_mode==2))
        {
            brightness=64;
            FILL_MATRIX_MONO(255,0,255);
            DATA_UPDATE();
            channel_data[0][0]=255;
            channel_data[1][0]=0;
            channel_data[2][0]=255;
            ch_speed=5;                                     //допустимые параметры: 1,3,5,15,17,51
            
           
            while((mode==5)&(sub_mode==2))
            {
                counter+=ch_speed;
                
                for(n_data=OUTPUTS_PER_CH-1;n_data>0;n_data--)
                {
                    channel_data[0][n_data]=channel_data[0][n_data-1];
                    channel_data[1][n_data]=channel_data[1][n_data-1];
                    channel_data[2][n_data]=channel_data[2][n_data-1];
                }
                if(counter>=1530)
                {
                    counter=0;
                }
                
                //сбрасываем всю подсветку
                if(data_changed==1)
                {
                    counter=1530;
                    channel_data[0][0]=255;
                    channel_data[1][0]=0;
                    channel_data[2][0]=255;
                    FILL_MATRIX_MONO(255,0,255);
                    DATA_UPDATE();                    
                    data_changed=0;
                }
                
                //здесь по этапам меняем параметры цветовых каналов
                if(counter<255)
                {
                    channel_data[0][0]=255;
                    channel_data[1][0]=0;
                    channel_data[2][0]-=ch_speed;
                }
                else if((counter>=255)&(counter<510))
                {
                    channel_data[0][0]=255;
                    channel_data[1][0]+=ch_speed;
                    channel_data[2][0]=0;
                }
                else if((counter>=510)&(counter<765))
                {
                    channel_data[0][0]-=ch_speed;
                    channel_data[1][0]=255;
                    channel_data[2][0]=0;
                }
                else if((counter>=765)&(counter<1020))
                {
                    channel_data[0][0]=0;
                    channel_data[1][0]=255;
                    channel_data[2][0]+=ch_speed;
                }
                else if((counter>=1020)&(counter<1275))
                {
                    channel_data[0][0]=0;
                    channel_data[1][0]-=ch_speed;
                    channel_data[2][0]=255;
                }
                else if((counter>=1275)&(counter<1530))
                {
                    channel_data[0][0]+=ch_speed;
                    channel_data[1][0]=0;
                    channel_data[2][0]=255;
                }
               
                
                DATA_UPDATE();
                DELAY(100000);
            }
        }
    }
}


void SNAKES(void)
{
    //бегущий огонёк
    
    brightness=128;
    color[0]=512;
    color[1]=0;
    color[2]=512;
    counter=0;
    
    while(mode==6)
    {
        //по всему дереву одновременно пробегает один огонёк
        if((mode==6)&(sub_mode==0))
        {
            for(n_channel=0;n_channel<3;n_channel++)                        //заливаем матрицу
            {
                for(n_data=0;n_data<OUTPUTS_PER_CH;n_data++)
                {         
                     //делаем так, чтобы яркость цвета гасла при удалении от первого листа                   
                    channel_data[n_channel][n_data]=color[n_channel]/(n_data*n_data/4+1); 
                }        
            }   
            DATA_UPDATE();
            
            while((mode==6)&(sub_mode==0))
            {
                //передаём по цепи значения соседних каналов, так возникает эффект движения
                for(n_channel=0;n_channel<3;n_channel++)
                {
                    channel_data[n_channel][OUTPUTS_PER_CH-1]=channel_data[n_channel][0];
                    
                    for(n_data=0;n_data<(OUTPUTS_PER_CH-1);n_data++)
                    {
                        channel_data[n_channel][n_data]=channel_data[n_channel][n_data+1];            
                    }                     
                }
                if(data_changed==1)
                {
                    //в случае изменения данных перезалить по-новой всё в драйвера
                    data_changed=0;
                    for(n_channel=0;n_channel<3;n_channel++)                       
                    {
                        for(n_data=0;n_data<OUTPUTS_PER_CH;n_data++)
                        {         
                             channel_data[n_channel][n_data]=color[n_channel]/(n_data*n_data/4+1);                      
                        }        
                    }   
                    
                }
                DATA_UPDATE();
                DELAY(100000);                
            }
        }
        
        //одновременно два пробегающих огонька по всему дереву
        if((mode==6)&(sub_mode==1))
        {
            for(n_channel=0;n_channel<3;n_channel++)                        //заливаем матрицу
            {
                for(n_data=0;n_data<OUTPUTS_PER_CH;n_data++)
                {
                    //в даном случае создаются два огня с разбросом в половину числа светодиодов, от каждого создаётся свой "шлейф"
                    if(n_data<OUTPUTS_PER_CH/2)
                    {
                        channel_data[n_channel][n_data]=color[n_channel]/(n_data*n_data/4+1);            
                    }
                    else
                    {
                        channel_data[n_channel][n_data]=color[n_channel]/((n_data-OUTPUTS_PER_CH/2)*(n_data-OUTPUTS_PER_CH/2)/4+1);           
                    }
                }        
            }   
            DATA_UPDATE();
            
            while((mode==6)&(sub_mode==1))
            {
                for(n_channel=0;n_channel<3;n_channel++)                       
                {
                    channel_data[n_channel][OUTPUTS_PER_CH-1]=channel_data[n_channel][0];
                    
                    for(n_data=0;n_data<(OUTPUTS_PER_CH-1);n_data++)
                    {
                        channel_data[n_channel][n_data]=channel_data[n_channel][n_data+1];            
                    } 
                    
                }
                if(data_changed==1)
                {
                   data_changed=0;
                   for(n_channel=0;n_channel<3;n_channel++)  
                   {
                       for(n_data=0;n_data<OUTPUTS_PER_CH;n_data++)
                       {
                           if(n_data<OUTPUTS_PER_CH/2)
                           {
                               channel_data[n_channel][n_data]=color[n_channel]/(n_data*n_data/4+1);            
                           }
                           else
                           {
                               channel_data[n_channel][n_data]=color[n_channel]/((n_data-OUTPUTS_PER_CH/2)*(n_data-OUTPUTS_PER_CH/2)/4+1);           
                           }
                       }        
                   }                       
                }
                DATA_UPDATE();
                DELAY(100000);                
            }
        }
    
    }
}    
    
    
void FIREWORK(void)
{
    brightness=64;
    
    counter=0;
    
    
    while(mode==7)
    {
        
        //Имитация взлёта ракеты     
        
        for(n_channel=0;n_channel<3;n_channel++)                        
        {
            for(n_data=0;n_data<OUTPUTS_PER_CH;n_data++)
            {
                //повторяем идею создания светящейся точки со шлейфом
                    channel_data[n_channel][n_data]=color[n_channel]/(n_data*n_data/4+1);           
            }        
        }   
        DATA_UPDATE();
        
        //быстро передаём от старшего номера к младшему, так, чтобы выглядело будто взлёт ракеты
        
        for(counter=OUTPUTS_PER_CH-1;counter>0;counter--)
        {
            for(n_channel=0;n_channel<3;n_channel++)                       
            {
                channel_data[n_channel][OUTPUTS_PER_CH-1]=channel_data[n_channel][0];
                
                for(n_data=0;n_data<(OUTPUTS_PER_CH-1);n_data++)
                {
                    channel_data[n_channel][n_data]=channel_data[n_channel][n_data+1];            
                } 
                
            }
            DATA_UPDATE();
            
        }
          
        // Здесь дожимаем подсветку до нуля, заодно эффект задержки между трассой ракеты и взрывом
        
        for(counter=OUTPUTS_PER_CH-1;counter>0;counter--)
        {
            for(n_channel=0;n_channel<3;n_channel++)                        //заливаем матрицу
            {
                channel_data[n_channel][OUTPUTS_PER_CH-1]=0;
                
                for(n_data=0;n_data<(OUTPUTS_PER_CH-1);n_data++)
                {
                    channel_data[n_channel][n_data]=channel_data[n_channel][n_data+1];            
                } 
                
            }
            DATA_UPDATE();
            
        }
        
        //вспышка и угасание подсветки      
            
        FILL_MATRIX_MONO(color[0],color[1],color[2]);
        DATA_UPDATE();
        
        uint8_t color_0, color_1, color_2;
        
        for(counter=0;counter<100;counter++)
        {
            color_0=color[0]-(color[0]*(100-counter)/100);
            color_1=color[1]-(color[1]*(100-counter)/100);
            color_2=color[2]-(color[2]*(100-counter)/100);
            FILL_MATRIX_MONO(color_0,color_1,color_2);
            DATA_UPDATE();
            DELAY(1000);
            
        }
        for(counter=0;counter<100;counter++)
        {
            color_0=color[0]-(color[0]*counter/100);
            color_1=color[1]-(color[1]*counter/100);
            color_2=color[2]-(color[2]*counter/100);
            FILL_MATRIX_MONO(color_0,color_1,color_2);
            DATA_UPDATE();
            DELAY(30000);
            
        }
      }

}    
    
 void RANDOM_COLOR(void)
 {
    //запуск случайного цвета, далее угасание каналов листа и рандомный выбор очередного цвета для каждого листа
     
     brightness=128;  
       for(n_data=0;n_data<OUTPUTS_PER_CH;n_data++)
       {
          channel_data[0][n_data]=(rand()/4194304/20)*20;   //деление и умножение на 20 для большей контрастностимежду листами
          channel_data[1][n_data]=(rand()/4194304/20)*20;
          channel_data[2][n_data]=(rand()/4194304/20)*20;
                
       }
       DATA_UPDATE();
     
     
    while(mode==8)
    {
       for(n_data=0;n_data<OUTPUTS_PER_CH;n_data++)
       {
           
               
               if((channel_data[0][n_data]==0)&                         //если свет на листе совсем погас, то
                  (channel_data[1][n_data]==0)&
                  (channel_data[2][n_data]==0))
               {
                   channel_data[0][n_data]=(rand()/4194304/120)*120;    //обновляем его цвет
                   channel_data[1][n_data]=(rand()/4194304/120)*120;
                   channel_data[2][n_data]=(rand()/4194304/120)*120;
               }
               
               for (n_channel=0;n_channel<3;n_channel++)
               {
                   if(channel_data[n_channel][n_data]!=0)
                   {              
                        channel_data[n_channel][n_data]--;
                   }
               }
               
               
           
       }
       DELAY(10000);
       DATA_UPDATE();       
    }



 }     
