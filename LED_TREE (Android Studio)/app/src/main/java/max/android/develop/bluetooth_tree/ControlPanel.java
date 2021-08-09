package max.android.develop.bluetooth_tree;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.OutputStream;

public class ControlPanel extends AppCompatActivity {
    public ConnectedThread threadCommand;
    Button btnBack;                                                     // кнопка назад
    SeekBar paletteSel;                                                 // слайдер изменения цвета
    SeekBar dispersionSel;                                              // слайдер изменения дисперсии
    SeekBar brightnessSel;                                              // слайдер изменения яркости
    LinearLayout coloredSquare;                                         // окошко выбора цвета
    int color=0xffff0000;

    TextView txtDispersion, txtPerDispersion;                           // текстовые поля выбранного значения дисперсии
    TextView txtBrightness,txtPerBrightness;                            // текстовые поля выбранного значения яркости



    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_panel);


        //Добавляем объекты внешнего вида
        coloredSquare = (LinearLayout) findViewById(R.id.coloredSquare);//выбраный цвет
        btnBack = (Button) findViewById(R.id.btnBack);//кнопка возврата в меню
        paletteSel = (SeekBar) findViewById(R.id.seekBarPalette);//ползунок выбора цвета
        dispersionSel = (SeekBar) findViewById(R.id.seekBarDispersion);//ползунок выбора разброса
        brightnessSel = (SeekBar) findViewById(R.id.seekBarBrightness);//ползунок выбора яркости

        txtDispersion = (TextView) findViewById(R.id.txtDispersion);
        txtPerDispersion = (TextView) findViewById(R.id.txtPerDispersion);
        txtBrightness = (TextView) findViewById(R.id.txtBrightness);
        txtPerBrightness = (TextView) findViewById(R.id.txtPerBrightness);

        //до тех пор, пока не выберется цвет хоть раз, не показываем панели выбора яркости и дисперсии
        brightnessSel.setVisibility(View.GONE);
        dispersionSel.setVisibility(View.GONE);
        txtDispersion.setVisibility(View.GONE);
        txtPerDispersion.setVisibility(View.GONE);
        txtBrightness.setVisibility(View.GONE);
        txtPerBrightness.setVisibility(View.GONE);

        //Запускаем прослушку изменений параметров элементов

        paletteSel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                updateBackground();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
                //как только хоть раз выбрали цвет, показываем панели выбора яркости и разброса
                brightnessSel.setVisibility(View.VISIBLE);
                dispersionSel.setVisibility(View.VISIBLE);
                txtDispersion.setVisibility(View.VISIBLE);
                txtPerDispersion.setVisibility(View.VISIBLE);
                txtBrightness.setVisibility(View.VISIBLE);
                txtPerBrightness.setVisibility(View.VISIBLE);

                int currentSeekBarValue =color;
                int rChanel,gChanel,bChanel;
                rChanel=currentSeekBarValue/0x10000;
                gChanel=(currentSeekBarValue/0x100)%0x100;
                bChanel=currentSeekBarValue%0x10000;
                threadCommand.sendCommand(77);//"M" in ASCII
                threadCommand.sendCommand(67);//"С" in ASCII
                threadCommand.sendCommand(rChanel);//R chanel
                threadCommand.sendCommand(gChanel);//G chanel
                threadCommand.sendCommand(bChanel);//B chanel
                threadCommand.sendCommand(83);//"S" in ASCII


            }
        });
        dispersionSel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int currentDispersion, boolean b)  { updateDispersionValue(); }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {       }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
                threadCommand.sendCommand(77);//"M" in ASCII
                threadCommand.sendCommand(68);//"D" in ASCII
                threadCommand.sendCommand((dispersionSel.getProgress()));
                threadCommand.sendCommand((dispersionSel.getProgress()));
                threadCommand.sendCommand((dispersionSel.getProgress()));
                threadCommand.sendCommand(83);//"S" in ASCII
            }
        });

        brightnessSel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b)
            {
                updateBrightnessValue();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
                threadCommand.sendCommand(77);//"M" in ASCII
                threadCommand.sendCommand(66);//"B" in ASCII                // отправляем B (Brightness)
                threadCommand.sendCommand((brightnessSel.getProgress()));           // отправляем текущую яркость
                threadCommand.sendCommand(83);//"S" in ASCII2               // отправляем S (Stop)
            }
        });



        //добавляем обработку кнопки возврата и устанавливаем действие на клик
        btnBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //объект для запуска новых активностей
                Intent intent = new Intent();
                //связываем с активностью управления
                intent.setClass(getApplicationContext(), MainActivity.class);
                //закрыть эту активность, открыть новую
                startActivity(intent);
            }
        });

        //создаем новый поток, он будет занят отправкой данных
        //в качестве параметра передаем сокет, созданный в активности подключения Bluetooth устройства
        //код потока представлен ниже
        threadCommand = new ConnectedThread(BluetoothConnection.clientSocket);
        threadCommand.run();
    }


    private void updateBackground()    //разобьём всю шкалу на 5 зон в связи с разным изменением параметров каналов цвета
    {

        int currentSeekBarValue =paletteSel.getProgress();

        if (currentSeekBarValue<=255)
        {
            color=0xff0000+(256*currentSeekBarValue);                       //переход с красного в жёлтый, R=255, G растёт, B=0
        }
        else if  (currentSeekBarValue>255 & currentSeekBarValue<=510)
        {
            color=0xffff00-((currentSeekBarValue-255)*65536);               //переход с жёлтого в зелёный , R уменьшается, G=255, B=0
        }
        else if(currentSeekBarValue>510 & currentSeekBarValue<=765)
        {
            color=0x00ff00+(currentSeekBarValue-510);                       //переход с зелёного в голубой R=0, G=255, B растёт
        }
        else if(currentSeekBarValue>765 & currentSeekBarValue<=1020)
        {
            color=0x00ffff-((currentSeekBarValue-765)*256);                 //переход с голубого в синий R=0, G уменьшается, B=255
        }
        else if(currentSeekBarValue>1020 & currentSeekBarValue<=1275)
        {
            color=0x0000ff+((currentSeekBarValue-1020)*65536);              //переход из синего в фиолетовый R растёт, G=0, B=255
        }
        else if(currentSeekBarValue>1275 & currentSeekBarValue<=1530)
        {
            color=0xff00ff-(currentSeekBarValue-1275);              //переход из фиолетового в красный R =255, G=0, B уменьшается
        }
        else
        {color=0;}

        //Зададим код цвета
        TextView selectedColor = (TextView) findViewById(R.id.txtOfColorCode);
        selectedColor.setText("#"+String.format("%06X", (color & 0xffffff))); //отображение в HEX формате с добавлением нулей до 6ти цифр

        coloredSquare.setBackgroundColor(0xff000000+color);                    //устанавливаем новый фон слоя квадрата
    }

    private void updateDispersionValue()
    {
        int currentDispersion =dispersionSel.getProgress();
        TextView selectedColor = (TextView) findViewById(R.id.txtPerDispersion);
        selectedColor.setText(currentDispersion +"%");
    }

    private void updateBrightnessValue()
    {
        int currentBrightnessValue = ((brightnessSel.getProgress())*100/255);
        TextView selectedColor = (TextView) findViewById(R.id.txtPerBrightness);
        selectedColor.setText(currentBrightnessValue +"%");
    }


    private static class ConnectedThread extends Thread {
        private final BluetoothSocket socket;
        private final OutputStream outputStream;

        public ConnectedThread(BluetoothSocket btSocket) {
            //получаем сокет
            this.socket = btSocket;
            //создаем стрим - нить для отправки данных
            OutputStream os = null;
            try {
                os = socket.getOutputStream();
            } catch(Exception e) {}
            outputStream = os;
        }




        public void sendCommand(int onSend) {

            try {

                outputStream.write(onSend);

            } catch(Exception e) {}
        }

    }

}