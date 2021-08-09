package max.android.develop.bluetooth_tree;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Themes extends AppCompatActivity {

    Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_themes);

        //добавляем кнопку возврата в главное меню
        Button btnBack = (Button) findViewById(R.id.Back);
        Button btnFireEffects = (Button) findViewById(R.id.FireEffects);
        Button btnSeasons = (Button) findViewById(R.id.Seasons);
        Button btnGradients = (Button) findViewById(R.id.Gradients);
        Button btnSnakes = (Button) findViewById(R.id.Snakes);
        Button btnFirework = (Button) findViewById(R.id.Firework);
        Button btnRandomColors= (Button) findViewById(R.id.RandomColors);

        //добавляем обработку кнопки возврата
        //устанавливаем действие на клик
        btnBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //объект для запуска новых активностей
                Intent intent = new Intent();
                //связываем с активностью управления
                intent.setClass(getApplicationContext(), MainActivity.class);
                //закрыть эту активность, открыть экран управления
                startActivity(intent);
            }
        });

        btnSeasons.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //объект для запуска новых активностей
                Intent intent = new Intent();
                //связываем с меню Свечи
                intent.setClass(getApplicationContext(), Seasons.class);
                //закрыть эту активность, открыть экран управления
                startActivity(intent);
            }
        });

        btnFireEffects.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //объект для запуска новых активностей
                Intent intent = new Intent();
                //связываем с меню огненных эффектов
                intent.setClass(getApplicationContext(), FireStyles.class);
                //закрыть эту активность, открыть выбрвнную
                startActivity(intent);
            }
        });

        btnGradients.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //объект для запуска новых активностей
                Intent intent = new Intent();
                //связываем с меню огненных эффектов
                intent.setClass(getApplicationContext(), Gradients.class);
                //закрыть эту активность, открыть выбрвнную
                startActivity(intent);
            }
        });

        btnSnakes.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //объект для запуска новых активностей
                Intent intent = new Intent();
                //связываем с меню огненных эффектов
                intent.setClass(getApplicationContext(), Snakes.class);
                //закрыть эту активность, открыть выбрвнную
                startActivity(intent);
            }
        });

        btnFirework.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //объект для запуска новых активностей
                Intent intent = new Intent();
                //связываем с меню огненных эффектов
                intent.setClass(getApplicationContext(), Firework.class);
                //закрыть эту активность, открыть выбрвнную
                startActivity(intent);
            }
        });

        btnRandomColors.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //объект для запуска новых активностей
                Intent intent = new Intent();
                //связываем с меню огненных эффектов
                intent.setClass(getApplicationContext(), RandomColor.class);
                //закрыть эту активность, открыть выбрвнную
                startActivity(intent);
            }
        });



    }


}