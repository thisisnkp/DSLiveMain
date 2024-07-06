package com.example.dsLive.utils.Filters;


import com.example.dsLive.R;

import java.util.ArrayList;
import java.util.List;

public class FilterUtils {


    public static List<FilterRoot> getFilters() {
        List<FilterRoot> filterRoots = new ArrayList<>();
        filterRoots.add(new FilterRoot("None"));
//        filterRoots.add(new FilterRoot("livegif1"));
//        filterRoots.add(new FilterRoot("livegif2"));
//        filterRoots.add(new FilterRoot("livegif3"));
//        filterRoots.add(new FilterRoot("livegif4"));
//        filterRoots.add(new FilterRoot("livegif6"));
        filterRoots.add(new FilterRoot("circle"));
        filterRoots.add(new FilterRoot("earth"));
        return filterRoots;
    }

    public static List<FilterRoot> getFilter2() {
        List<FilterRoot> filterRoots = new ArrayList<>();
        filterRoots.add(new FilterRoot("None"));
//        filterRoots.add(new FilterRoot("bubble"));
//        filterRoots.add(new FilterRoot("fires"));
//        filterRoots.add(new FilterRoot("heartsfilter"));
        filterRoots.add(new FilterRoot("filter1"));
        filterRoots.add(new FilterRoot("pattern"));
        filterRoots.add(new FilterRoot("filter2"));
        filterRoots.add(new FilterRoot("fi"));
        return filterRoots;
    }


    public static int getDraw(String title) {
        if (title.equalsIgnoreCase("None")) {
            return 0;
        } else if (title.equalsIgnoreCase("livegif1")) {
            return R.raw.livegif1;
        } else if (title.equalsIgnoreCase("livegif2")) {
            return R.raw.livegif2;
        } else if (title.equalsIgnoreCase("livegif3")) {
            return R.raw.livegif3;
        } else if (title.equalsIgnoreCase("livegif4")) {
            return R.raw.livegif4;
        } else if (title.equalsIgnoreCase("livegif5")) {
            return R.raw.livegif5;
        } else if (title.equalsIgnoreCase("livegif6")) {
            return R.raw.livegif6;
        } else if (title.equalsIgnoreCase("bubble")) {
            return R.raw.bubble;
        } else if (title.equalsIgnoreCase("fires")) {
            return R.raw.fires;
        } else if (title.equalsIgnoreCase("heartsfilter")) {
            return R.raw.heartsfilter;
        }else if (title.equalsIgnoreCase("filter1")) {
            return R.raw.demoan;
        }else if (title.equalsIgnoreCase("pattern")) {
            return R.raw.pattern;
        }else if (title.equalsIgnoreCase("filter2")) {
            return R.raw.heart2;
        }else if (title.equalsIgnoreCase("circle")) {
            return R.raw.circle;
        }else if (title.equalsIgnoreCase("fi")) {
            return R.raw.fi;
        }else if (title.equalsIgnoreCase("earth")) {
            return R.raw.earth;
        }
        return 0;
    }


}
