package com.example.android.stockkeepingassistant.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.stockkeepingassistant.R;
import com.example.android.stockkeepingassistant.model.Product;
import com.example.android.stockkeepingassistant.view.ui.ProductActivity;

import java.util.List;

public class CatalogAdapter extends RecyclerView.Adapter<CatalogAdapter.ProductHolder> {
    class ProductHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView title;
        private TextView price;
        private TextView quantity;
        private Button sellButton;

        ProductHolder(View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.product_image_view);
            title = itemView.findViewById(R.id.list_item_product_title);
            price = itemView.findViewById(R.id.list_item_product_price);
            quantity = itemView.findViewById(R.id.list_item_product_quantity);
            sellButton = itemView.findViewById(R.id.list_item_sell_button);
        }

        void bindData(String productTitle, String productPrice, String productQuantity) {
            image.setImageResource(R.mipmap.ic_launcher); // TODO: 4/10/18 Set the image from the photo file if exists
            title.setText(productTitle);
            price.setText(productPrice);
            quantity.setText(productQuantity);
        }
    }

    private List<Product> products;
    private Context context;

    public CatalogAdapter(Context context, @Nullable List<Product> products) {
        this.products = products;
        this.context = context;
    }

    @NonNull
    @Override
    public ProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        return new ProductHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductHolder holder, int position) {
        Product product = products.get(position);
        holder.itemView.setOnClickListener(v -> {
          Intent intent = new Intent(context, ProductActivity.class);
          context.startActivity(intent);
        });
        holder.bindData(product.getTitle(), product.getPrice().toString(), Integer.toString(product.getQuantity()));
    }

    @Override
    public int getItemCount() {
        return products == null ? 0 : products.size();
    }
}
