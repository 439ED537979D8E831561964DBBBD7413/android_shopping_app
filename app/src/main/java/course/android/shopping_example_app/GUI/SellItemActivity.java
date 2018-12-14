package course.android.shopping_example_app.GUI;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import course.android.shopping_example_app.Logic.SysData;
import course.android.shopping_example_app.Objects.Item;
import shopping_example_app.R;

public class SellItemActivity extends AppCompatActivity {

    private final static int IMAGE_CAPTUE_REQ = 1;
    private static final int P_WIDTH = 500;
    private static final int P_HEIGHT = 300;
    private ImageView imageView;
    private Spinner category;
    private EditText name, desc, price;
    private SysData data;
    private Bitmap img;
    private Item item;
    private String uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_item);
        data = SysData.getInstance();

        int index = getIntent().getIntExtra("index",-1);
        if (index >= 0){
            item = data.getItem(index);
        }

        // init views
        imageView = (ImageView)findViewById(R.id.sell_img);
        category = (Spinner)findViewById(R.id.spinner_sell_cat);
        name = (EditText)findViewById(R.id.edit_sell_name);
        desc = (EditText)findViewById(R.id.edit_sell_desc);
        price = (EditText)findViewById(R.id.edit_sell_price);
        category.setAdapter(ArrayAdapter.createFromResource(this,R.array.categories,
                R.layout.support_simple_spinner_dropdown_item));
        initVItemView();


    }

    /**
     * init item view if item to be edited
     */
    private void initVItemView() {
        if (item != null){
            int s;
            imageView.setImageBitmap(item.getImage());
            img = item.getImage();
            uri = item.getUri();
            String str = item.getCategory().getType();

            for(s = 0; s < category.getCount(); s++)
                if (category.getItemAtPosition(s).equals(str)) break;

            category.setSelection(s);
            name.setText(item.getName());
            desc.setText(item.getDesc());
            price.setText(String.valueOf(item.getPrice()));

        }
    }

    /**
     * open camera to capture photos
     * @param view
     */
    public void onClickCam(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        startActivityForResult(intent,IMAGE_CAPTUE_REQ);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if (requestCode == IMAGE_CAPTUE_REQ ) { // if image was captured from camera
                // restore photo from intent
                img = getImage((Bitmap) data.getExtras().get("data"));
                uri = data.getDataString();

            }
            imageView.setImageBitmap(img);

        }
    }

    private Bitmap getImage(Bitmap img) {

        Bitmap newImage = Bitmap.createScaledBitmap(img, P_WIDTH, P_HEIGHT, false);

        return Bitmap.createBitmap(newImage, 0, 0, newImage.getWidth(), newImage.getHeight(), null, false);

    }

    /**
     * Put on sale button clicked
     * @param view
     */
    public void publishSale(View view) {
        String iName = name.getText().toString(),
                iDesc = desc.getText().toString(),
                iCate = (String)category.getSelectedItem(),
                sPrice = price.getText().toString();

        if(!iName.isEmpty() && !iDesc.isEmpty() && !iCate.isEmpty() && !sPrice.isEmpty() && img != null ) {
            long iPrice = Long.valueOf(sPrice);
            if (item == null) {
                if (data.sellItem(img, iPrice, iName, iDesc, iCate, uri)) {
                    Toast.makeText(this, R.string.putonsale, Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                } else
                    Toast.makeText(this, R.string.notputonsale, Toast.LENGTH_SHORT).show();
            } else {
                if (data.updateItems(item, img, iPrice, true, iName, iDesc, iCate, uri)) {
                    Toast.makeText(this, R.string.itemupdated, Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                } else
                    Toast.makeText(this, R.string.itemnotupdated, Toast.LENGTH_SHORT).show();
            }

            finish();
        }else{
            Toast.makeText(this, R.string.emptyFields, Toast.LENGTH_SHORT).show();
        }

    }
}
