package com.amanse.anthony.fitcoinandroid;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;

public class ArticlePagerAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<ArticleModel> articleModels;

    public ArticlePagerAdapter(Context context, ArrayList<ArticleModel> articleModels) {
        this.context = context;
        this.articleModels = articleModels;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.article_item, container, false);

        final ArticleModel articleModel = articleModels.get(position);

        TextView articleTitle = view.findViewById(R.id.articleTitle);
        TextView articleSubTitle = view.findViewById(R.id.articleSubtitle);
        TextView articleSubtext = view.findViewById(R.id.articleSubtext);
        TextView articleDescription = view.findViewById(R.id.articleStatement);
        ImageView articleImage = view.findViewById(R.id.articleImage);
        Button articleLink = view.findViewById(R.id.articleLink);

        articleTitle.setText(articleModel.getTitle());
        articleSubTitle.setText(articleModel.getSubtitle());
        articleSubtext.setText(articleModel.getSubtext());
        articleDescription.setText(articleModel.getDescription());
        if (articleModel.getBitmap() != null) {
            articleImage.setImageBitmap(articleModel.getBitmap());
        }

        articleLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(articleModel.getLink());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);
            }
        });

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return articleModels.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
