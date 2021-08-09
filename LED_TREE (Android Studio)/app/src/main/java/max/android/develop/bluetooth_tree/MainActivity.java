package max.android.develop.bluetooth_tree;



import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity
{

    Button btnConnectBt;
    Button btnTheme;
    Button btnCustom;


    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState); //обязательная строчка
        //прикрепляем ранее созданную разметку
        setContentView(R.layout.activity_main);

        //находим Viev элементы кнопок
        btnConnectBt=(Button) findViewById(R.id.BtConnection);
        btnTheme=(Button) findViewById(R.id.Themes);
        btnCustom=(Button) findViewById(R.id.Custom);

        //устанавливаем действие на клик
        btnConnectBt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //объект для запуска новых активностей
                Intent intent = new Intent();
                //связываем с активностью управления
                intent.setClass(getApplicationContext(), BluetoothConnection.class);
                //закрыть эту активность, открыть панель подключения по Bluetooth
                startActivity(intent);
            }
        });

        //также и для остальных кнопок
        btnTheme.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //объект для запуска новых активностей
                Intent intent = new Intent();
                //связываем с активностью управления
                intent.setClass(getApplicationContext(), Themes.class);
                //закрыть эту активность, открыть меню тем
                startActivity(intent);
            }
        });

        btnCustom.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //объект для запуска новых активностей
                Intent intent = new Intent();
                //связываем с активностью управления
                intent.setClass(getApplicationContext(), ControlPanel.class);
                //закрыть эту активность, открыть экран ручной настройки цвета
                startActivity(intent);
            }
        });

    }
}