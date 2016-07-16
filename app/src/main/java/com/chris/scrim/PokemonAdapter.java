package com.chris.scrim;

/**
 * Created by SSharifian on 7/16/2016.
 */
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class PokemonAdapter extends ArrayAdapter {

    private List<String> pokemonList = new ArrayList<String>();

    public PokemonAdapter(Context context, int resource) {
        super(context, resource);
    }

    public void add(String pokemoName) {
        pokemonList.add(pokemoName);
        super.add(pokemoName);
    }

    @Override
    public int getCount() {
        return this.pokemonList.size();
    }

    @Override
    public String getItem(int position) {
        return this.pokemonList.get(position);
    }
}
