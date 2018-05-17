package com.amanse.anthony.fitcoinandroid;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 */
public class TechFragment extends Fragment {

    public String BACKEND_URL="https://anthony-blockchain.us-south.containers.mybluemix.net";
    public String TAG="FITNESS_TECH_FRAG";

    ArrayList<ArticleModel> articles;
    ArrayList<ArticleModel> articlesDefault;
    ViewPager viewPager;
    ArticlePagerAdapter adapter;
    public RequestQueue queue;
    Gson gson = new Gson();

    public TechFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        View rootView = inflater.inflate(R.layout.fragment_tech, container, false);

        // request queue
        queue = Volley.newRequestQueue((AppCompatActivity) getActivity());

        viewPager = rootView.findViewById(R.id.articlePager);

        articles = new ArrayList<>();
        articlesDefault = new ArrayList<>();

        // default data in case no internet or server down
        articlesDefault.add(gson.fromJson("{\"page\":1,\"link\":\"https://developer.ibm.com/code/2018/03/16/building-a-secret-map-an-experiment-in-the-economy-of-things/\",\"title\":\"Kubecon\",\"subtitle\":\"2018\",\"image\":\"kubecoin logo\",\"subtext\":\"\",\"description\":\"\",\"imageEncoded\":\"iVBORw0KGgoAAAANSUhEUgAAASwAAAEsCAIAAAD2HxkiAAAACXBIWXMAAAsSAAALEgHS3X78AAAg\n" +
                "AElEQVR42u19e1BbZ5bn192QcKVIY0QhlkFOSR5I1pKsbIldSwT3jA0Z4zjGO9U2Dt4lW9UBMtsz\n" +
                "ZRzY6Uwcuwfc7dekeyBAzaMCynRtmDE2dmpiHMe4A7inTUDeCpmRJVExFNK2pbCSY+GRIi7Tojr7\n" +
                "xzHX13pZz8uVOL9KpbAQetz7/b7zO4/vnG998803BIFArB2+jZcAgUASIhBIQgQCgSREIJCECAQC\n" +
                "SYhAIAkRCASSEIFAEiIQCCQhAoEkRCAQSEIEAkmIQCCQhAgEkhCBQCAJEQgkIQKBQBIiEEhCBAKB\n" +
                "JEQgkIQIBAJJiEAgCREIBJIQgUASIhAIJCECgSREIBBIQgQCSYhAIJCECASSEIFAIAkRCCQhAoFA\n" +
                "EiIQSEIEAoEkRCCQhAgEAkmIQCAJEQgEkhCBQBIiEAgkIQKBJEQgEEhCBAJJiEAgkIQIBJIQgUAg\n" +
                "CREIJCECgUASIhBIQgQCgSREIHiLHLwEmQLPpJn5+R7rZzZyxUKxSvHg1rJ+RiAJEbEi4PX7LDba\n" +
                "4V664/ZZbAGv32uxrfiWknlNSlZIyaTURiklk4pVCvg/Xmr+4FvffPMNXoW1ZZ1n0uy12DyTZtrh\n" +
                "ph13Iz1TpJTnioXws1ilyFn9mQ3a4abvuB/+HPXVBDKppEItVikkFWq8EUjC9QXa4fZMmu9Nmj2T\n" +
                "5lCeAD1EKoVgo5SSSZNXlaBjQcFGorpIKS+oUEsq1JIKdW44eiOQhFni1LlGjPcmzT6rnf24RK8S\n" +
                "qxRilUKkVHAmFMH8eq220I1AolcV1ejASOJdQxJmA1wjRteI0XXVyHbtJHqVpEINxocnkhiMM3uD\n" +
                "oGSFRTW6kroqZCOSMCPhtdjshmE29yhZoaRCDUaGt5IPpLLrqtF17SabjSV1VbIDVZRMincWSch3\n" +
                "BLx+5/kxu2GY0XiZa09CbbhEr5IdqCqpq8IbjSTkr+lzDo3DP3NEgqJdOlldVRbEHp1DY2zbmCMS\n" +
                "yJtq0TAiCfllMez9w54pC/xTpJQrmmqlNbosCzPSDrfj/JhzaIwx8iV1O8ra6pGKSMI1NhGznYPs\n" +
                "RSlvrM36MEbQpiPRq8ra6jHZiCRcS/qBPJM31a6rDFuQ/EYqIgm5g2fSbO0wQCh/fdIvSKPOdg4y\n" +
                "VESBiiRM+4IztfaADEP6RaGivHFPaVs9XhkkYSoR8PrnOgfthsvcLzKHbYH20x7X/XvuRULIrHme\n" +
                "EKLRKXfsrQz7ZNOUFZ5DCClTb5IpijV6JWcCdabDwGxSyuONmMxAEqYsDmFq7YFcWdHOrZuPN3Ig\n" +
                "t7qP9s2ZbZF+W6pWHD7ZHPTgQPcF49h06JM1OmXzWw1cynVTWw94yxK9anNHIxbcRAceZXqMyppp\n" +
                "N0B+jJIVajpb+BB4kEg3yBTFMTKQELLkp0MfNE1ZHbYFQgglzHtOr5RI81P28SrU2yffne0chAjq\n" +
                "xK620taXy9rqcTkhCeOGvX94tnMQDGDKl5HDtjB7a37ObAOGaHRKfXU5JcwLfSYlyNu+t7Jsi4IQ\n" +
                "IlP8btjnzJrnGQZSgrx9TXt01Vravzx7a/7iqoRmv/VA9wWnbYF55APDR6Vqxb6mPaHcThhlbfWy\n" +
                "A1Wwhc11nXONGDWdLWgSUY7G4QHeau0BAyhSylO4esBhMxmtHvf96ArzYv/l68Ofws+9H56K/rJ9\n" +
                "pwZMRiv83HKyqUy96aEx9y/fcy8y7KL9y2de7wl9d2Bvy6nmFPIwVMyjSURLGKtL81nj6ZQvGodt\n" +
                "oeetPnppOdIT5sw2h22B4QAlpIK1sX/ZYfuSEDJ7y1a2RcFm2uyteUapsh8Htcnm1cX+ywwD4cm0\n" +
                "fxkITC8tX+y/HOpqJomiGt32qXdhU5vrOueZNGu6WjCHgSSMiNnOwbmucyk3gISQAmk+w0CJdMPu\n" +
                "+hfKtigk0nzj6PRAz4VVmtHs5zM/v3HwxyHsrWbIRvuXmd9qdNECobR/ma1a33ynBcQt40/OmW0e\n" +
                "9yLbPzRNWceHJ3YfrA7idlzIFQu1hiPOoTFru8EzZbmxs1XT1VJUo8P1BsBuaw8lqLHuGDCwpG6H\n" +
                "buhEMgz0uBcHui+8cfDHtH+ZsUglLCunq9bCWmfnD2SK330Y3ija8JA8IfaT7dGBeYxkP4PEMPPz\n" +
                "9r2VjHupq9Yyj0MihMH48MSc2dZztL/7aJ9xdDqZK1xSV7XtWpdIKV/xLU03nZnpMOCqQ0v4EF6L\n" +
                "zVh3bMW3lJLs1qx5vu/kADBn/NLE7oPVD9Z6lfYDw0dAITA4s+b5K2dHmWgKO+jCppNEukEizZcp\n" +
                "iikhVSDNlxRtiE62iCQ0PiShnkW8KJbTs8rJObNtzmy7MvjJ7voXdDH8bVhQMum2ka6ZDoPdcNlu\n" +
                "uOy12LSGI5jTRxISkEkrvqVUSVC2Qbt+aWLHqs0p2/JQ0b17aoD204x7Ri8tD/RcmBr7jBF+bEdO\n" +
                "V1XOMDkUbKHoedSOhfilX7JZzXIpwyckKWHe8b43YKeApKXHfX+g58LF/svb91buYNnSuACZQ5Cm\n" +
                "xrpjGDVd73J0tnPQ1Na74lsq2rk1YQlK+5fZSo8S5m1fLWehl5bHL02skrNYIt3A6MnQECUIPwdL\n" +
                "aob6iuGV3ipjTVNWRgCHE8n3Q7cJslqFE0pp5pGgkCm9tPzx4OiZ13uSkaa6oROUrNBntRvrjnki\n" +
                "tFFFEmY/TG09jBOYmC6i/ctXzo62N73dd3qAbYV27K2kBHmMMWSIwQ6clKoVDS37W042vf2Pf6Gr\n" +
                "0rKNZxC1GFrOmudnzfPjlyaunB1le2jMn9NLy91H+xhSzZrnu4/2RSEtfAWmNKckXH6C9i8z70UJ\n" +
                "8pj30lWVJ3PxxSpF5cgDF9F44EfOoTGUo+suDMNkAjWdhxJ2Ak1T1o8HR+HnK2dHGw7vZxtD+BUY\n" +
                "Q9CTZepNTPZvR20lE5VpOLyfiVsyoRFG7M2ZbYf+61tBby2RbmB8sx17K41j0xCtcdoWeo72h5eX\n" +
                "gjzwVNn7BWOoSYTg6vilCSYytH1v5e6D1fua9oxfmgjyKoPCqrEgVyzUDZ2Y6TA4h8ZNbb1gIdES\n" +
                "rhcGGuuOua7dzBEJEmMgs4h11VpGYRrHph9rDDV6JfMgO0wSVkMWRF3TQWr28MlmtjkNC4bzxrFp\n" +
                "4+g0mLjrLBKGjdYwT6AEeVA1Tgnzdh+sfsSrNM+3N/8Ugkzx8lDT2VJSt4MQYmrrNbX1rMMF+Z2O\n" +
                "jo51yECf1Z4jEuiGThRujzvQN35p4hcXf6mvfiDGBEKKoRPtX2YWeu4TOYHACii9lcCKd9EHv7Lf\n" +
                "vuNy3iWEeFyLf7j/DwghxtHpv//ZWYaHuqpyCOE4bAvw56VqhUSar9EpN2uf0VeVb9/7/Pa9lfU/\n" +
                "+KNHVvMTORq9Ul+tpYSUQEiJ8p+SSPPL1Jt0VVrFs08/MJ5F+RNXbzJbwC8u/tJktK4EVh5Yudrn\n" +
                "y7/7XNCXNY5OT98wwc/bdm0NeyCD9i//1Q//diWwMme2mYxWpfaZeIO3RTW6XLHwq19+7rPaaYd7\n" +
                "vaUQc9YzA+MNw9D+5YHuCyajlW1zdNXaK4OfgF0yjk2zTcSOvZXXV7WcacpKNy1TwjyNTslUqIQe\n" +
                "ldhe+zwTCN19sDpKUDQsJNL8KH8iUxQ3tOy/2H85NPEItaOhf3Jl8BO2bQ/7sgPdF5gXdNoW7sWv\n" +
                "Swkh8qba3N8Rmtp64VCiprMF5SgyMLwNBP4ELbLd9S88XLUsSRYUJoXiMijFZpw9dsDjeN8PwzIh\n" +
                "hdBVa9/sPvRifTUTgylRFL9YX82uVhu/NAG62jg6zYheXZU2LLWYa8Ki+u8m9tlK6qo0nYcIIc6h\n" +
                "8XWlS3OQgQkgKD8WxRhq9EomcuOwLWj0Sok0v0RRDEEUqN7U6JScnbtlrGUkg2kcnf7A8NEHho90\n" +
                "VVp29UzY53vcix8/6gdKpBuYi+OwLXhci3F9NfDP15s9zEEGxg6mWEy2KTiUv7v+BaYElB0mDUvd\n" +
                "HbWVS366bMumlB9ZSB7jwxNM8OZhREcX/sDhu6ceFAZRgjzqqTyP+z7zNNq/3HfqfY/7/vba53cf\n" +
                "fCH2tD6bh5RMuh5OXawLOTrTYUiMgQ7bQvfRPiZNx5yODQ08BIVJL/ZfBj+w562HabrnVm2Crlq7\n" +
                "Y28lDxlICDl8svnF+mrmuwDuuRfZ1QjMXsPsSi8erKa/XiasXD9zXOP68KfMIY94delc17n1kD/M\n" +
                "/uioqa0HtE3Fpb+Mi4HG0emf/3TQ7fxqZvq2vro894mc68Of+u5/TQgJikwC2GFS++07Hw+OTt8w\n" +
                "MbHHhpb97LI13iL3iZyyLZt27K0skOY7bF9CzNZ3/+vpGybj2GfP6ZWwAc2a5/+h5yIT1Nm5/w9+\n" +
                "8cE/g5EvkhWapqyX3h9hnEkIAscFsUqx4vXf//y2a+SmWKV4qlSGljBTYe8fXvUuDsWrQseHJ1ZT\n" +
                "2/cHui+w5ehA9wV2qRdj38Jm6ihBXvORhoSLntcKumrt8b43mo80lKoVbH+SrEaJmW/32luvOOYf\n" +
                "XBlJUT77txLphoRDTZs7Gh/kD1t7vBYbWsKMhGvEeOt/9RJCNre/+vQru+L9c6X2GePoNJgyl/Mu\n" +
                "Jcyb+XyWcQ6NY9PGsc887sX/ICtk1KlGr6T9tP32HWYJvlhf/crrdaE+ZKagSFaory4v26Kg/cub\n" +
                "tc+AMT/3t//ExHW//2f1sk3Fn/3KBI/U/+CP/ub437udX8FvW042FyTRvaaoRueZNPvnv1z48FdP\n" +
                "v7LrO08+kZULNWvbWzCnk0rqdiQcZDNNWftODzzeh1EU66q07HZJs+b5SP1gEvgitMMN/4dR2Ez/\n" +
                "+bDIEQnA5sNI7YIKNUyrT5m8Z12T7bXPg6GD/hqlaoVGp4TjWoSQF+vjznNGCaqJlPJtI11IwoxB\n" +
                "Cu8cu9cLQzn2KSQ2NDrlvqaXkuxcBhMCvRZb6EzfhAHMZCbUJ3OEzzRlhey8RLqBOZvf3vy2x32/\n" +
                "VK1g14K/+c4h/uynSEKuMd14GkpDt0+9m/yZ0TOv97JPsn+v8aUdeytnzfPG0WnTlJUpFmHanCUs\n" +
                "nmGeduhA+ZQjyYHYHvfixf6PXjxYzQR4g+rLKUHem92HUthG0TNpNh74EUmu2h5JyGkwZub4e4SQ\n" +
                "yqudKTkt6nEvnjncy5CNrbLgJOHU2Ge0f7nh8P4Esg6eSbNjaCxomDZnSMkI3lnzfNC5jYaW/SkP\n" +
                "REH7n5TUWiAJ0+4KTuxqg2CMvKk2HY4QCWkrmJhgDhrou7ZIZgTv+KUJxg8k6Wz4baw75pmyiJRy\n" +
                "3dCJbGqKkVUpioDXP910mhBStHNrChlICNHoldtrn2f+2XdyIMoB9se6fKa2nk9UDTPH3+MJAyHY\n" +
                "Y2rr/YXyv892Dga8/rj+VvBoO5yw1UIpgdZwJEck8Fntc52D2bRusypFcfv0+1/98l9yRALdhRMp\n" +
                "D2crtc/MmuchHrMSWLF98WvmNFPs9JvpMNxq601VuCXl+O1vAp4py6/fv/rbfw+IVYoYr6FsU3HZ\n" +
                "FoVpyroSWGl+q6FIVpiuxfrkE/naZ51D4/c/v51NGfzskaOM7647/5M0TYyg/cvtTW+DcxiX6Aqa\n" +
                "HJYRiHf2G7jHHNQkQLO2VEXdkISpFKITNa204668cc/mjsb0vREEISBAGuMHs/cPQyebTAQlKyxr\n" +
                "q+dbQPJGTavPai/auVVrOIIk5Atgd6RkhZUjXeneHWn/coxZeNeIcabDwB/HL5mwDa961zPhN23/\n" +
                "m1lwDD8bAjOeSTOM79R0tnCgT2JhYMDrn248Pd10JgsYSAjxTFmuV/zxLG/CIWKVQt64BzbfeMNI\n" +
                "SMK0wNphIISU1O3gw/BA2BSu61+DVm7ZhLmuc8a6Y7TDzYcPU9pWT8kKacdde/8wknCNYe8fhrOC\n" +
                "aXUF4xLGxgM/WpPMOzcm8cbOVteIcc0/Sa5YCHd8ruscT/aFdUrCgNcPGqmMqwny0T/MjZpWe8hQ\n" +
                "ziwDf8a5FNXoJHoVIcTU2oMkXDuB1DkIMyRSm5pPLFRwXf8abxOAqRcghsvGumNr7o9pulrAPmd0\n" +
                "I/0MJiHtcIPZUa61EHWNGKHMn6wnwDiXtZWClExa2voyISSju7NlMAlBiEr0qrWNxziHxqabzqw3\n" +
                "BgJ8VvuNna1re+xd3lSbIxLQjruZ240mU0notdge9K3oallbBsIQhXWLFd+Sse7YGvIwVywEZ2Q2\n" +
                "YwtKM5WEM6tpiTXMICMDecJDeVMtpCsy1BhmJAm9Fhu0eFjDppQ8YWDRzq2lrS9TaauZzgge5oqF\n" +
                "sBIy1BhmJAnthuG1NYM8YaC8cY/WcKSsrR4G/a1nHpbUVWWuMcw8EtION3iDa2UGPZNmnqhQJjGT\n" +
                "KxbKeFBjveJbmm46vVZ5C6gyz0RjmHkkhKtctHPrmphBr8X2WeNpnlwK9hWgNvKiupp23F2r/CET\n" +
                "JuVDQU82kzDg9buuGtlGgON3N7X18Ccbwc5Q86c9rs9qX5N6GiZMmnHVpBlGQuf5MSiRWZPc4K3W\n" +
                "Hl7VxLANDq/qJ51D42vCBNmBKkKIZ8qSWdWkGUZCCMko1sIMznYO8u1gBNv6QV9g/mDm+HvcG2dK\n" +
                "JoXO+ZllDDOJhJ5JM+24myMScH/Q22ux8fx0PA+P1a1JkAYCVI7zY0jCtMAxNMZIDo7XNzRx4xt8\n" +
                "LFPDw9px2nGX+7Zokgo1JStc8S1lUK4iY0gY8PohM8G9GZzrHOTnAXn+Hyq3Gy5zf75B3lhLCIEA\n" +
                "HpIwlXCPGAkhIqWc4+7LXouNt0cE2WmJHJGAnx/S1NbD8WZRtEtHCHFdu5kp4ZmMISFsbNynpHl7\n" +
                "RiZHJGCXKyiPN/Lzc3LfgYKSSYt2bs0gY5gZJAx4/RCZhE2OMziHxnh7Trdol46drJfW6HhrDLnv\n" +
                "QAHrxJEhbmFOZmlRLqtkAl6/td3An4sAJycjzRvMFQv/0PoP5EEM2X1v0gzBZJ58+Jl2A5c9QqU1\n" +
                "OkJ6fVY77XDzp1NjZpNwTbSovX+YD8UxJXU7imp0sXfXhDIGCF/RDrfrqpEPY2dc1256Js2clVjk\n" +
                "ioVFO7e6rt10XTWueeuTbJCja6JFoXP22rp8pa0vv2AZ0HS2JNzflpJJ5U212yff1Z3/CfREWkNw\n" +
                "XFoNhM+I3jMZQEK4jhxr0ZSYQZFSruk8pOk8FBcBgH7bp95NYQs5SYVaN3QiASrCRlB5tTP5o1Ic\n" +
                "t2NiYqT8T+RkAAmhKL6Aw2LRlJhBSla4baSrpK6qpK5KN3QCyqkev3R2bk0t/UKpqO1/M8ZDwLrz\n" +
                "P4FPIlYpylPh0XEpLiiZFDYO/hvDjLGEXI4cgDLx5BcB+5+azpboPMwRCbT9b2oNR9LdQLWoRlc5\n" +
                "0vXYTUHTeYjtwqVEhnCcuyvIEEXK98AM7XBDUIHLYxNQJp68+goKzWk6WwghYQekxTh91jNpvjdp\n" +
                "9llsSw53UY0u6FgzHOQTqxRipSIogREat9B0thRUqK3thrDbTeho+FQZsdnOQbgO3LiFdsPle0jC\n" +
                "lJhBLoMKrhFjqmKJxrpj2v4j7BKfsDwsqdsRfV16LTa7YThorn3YxgI+q91ntTvJ+Mzx90RKuayu\n" +
                "quRAVSRul9RViZSK0I6pYRk4c/y91Fzeq8ZAh5+bdumgnnxWe8Dr5/MkQ77LUdjGuDSDKTyXTTvu\n" +
                "TuxqC5ocpOlsYbdmis5Az6TZWHdsYlebc2g8iCqhqyroKvms9pnj732iajC19UQSgWKVQjd0ArL8\n" +
                "OSKBRK+qvNrJZqBn0nyjpjVVDCSErPiW3ByefM8It5DvlhDOpHEWlWHKxFMqbi87zo8pjzcyi7us\n" +
                "rb6srT7g9TvPj0XKYtEOt6m1B5rKJeviDo1Duiys8RSrFNuudYXm0wJe/0yHIR3ThR3nxzirwi+o\n" +
                "UPusds+kmc9jDHltCQNeP1SNibgq2k7TJr3iWzK19U43PnK+jmnHEIa3/cM3dramhIHMB5jrOnej\n" +
                "Jny3bEgnBlng6/rX0jTfm8uT76AO+NP7I/NICOflREo5Z4I+rSW/rms3jXXHHrvvTDeenjn+3mPD\n" +
                "sznxXxOf1W6sO/bYg3aeSXO6p7txVloNDnkKt7N1R0LYwDg7u8SU5qRxW7Hao/icAa/fWHcsxs8Q\n" +
                "elkEMTRcA5scvXiFg7pnzhqiUTIpeLx8Nob8JqHVRlKUoYpJJnHivptaI4ZJ6DvuZFrFxH6honxT\n" +
                "59BYmlRokCLlrJAFdis+ny3kNQlhRXIWleFmexarFJHYIlYptk+9C2fh0ofN7a/qhk5EcaK4ORLF\n" +
                "WcQSSIiWMPH9ksuoDDfLIvoYqVyxUGs4srn91XQwQaSUV17tjH6qIDRIkx0k9CEJEzGDq/qBm6gM\n" +
                "U5qTVsQ4P0PeVKsbOhGlZjrsr6KHauSNe7aNdMXiYMOQo3RfCs4KWeCC87mMm8ckvOMmHNbKcCNX\n" +
                "QjN1kRaHWKXYNtIFY2jDGsxIW34YcooEuvM/2RxhnnGos8QMOUoroJCFg2su4n2AlO+WkLPkBAck\n" +
                "DGsGZzoMQfnDINLqzv8kGbsExzIilRx5LbYbO1tDlWFJXRUHniE3EpFZQrw1hvwl4dIdd5Y5hNCK\n" +
                "L+hNnUPjkD+MtEQkFerKka6gaE2k8S9smZojEmxufzXKsQyoiVvxLYVtZsWBZ8iZIoXLwlu3kL8k\n" +
                "XOF230q3JQzbrJE5mgBp9EifAaI1ms5DYBIpWWEkuVhuOLK5/dXS1pdL6nZsu9YVhUjOoTEmIx92\n" +
                "rB8HTZY5SxvwuXqb8Ll2lMuq0YDXn+52MqENcmiHm52XBx7qhk5Ecu3gfPBjgxCxWLDQUxGh9Zxw\n" +
                "KDatzeY4m59BbZSSKcs9DpvcZIkl5BIcCJXQBjmh8xJg2G26hbGprSf0VETYes50d9biLHfH84Zr\n" +
                "fA/M5HAiJNLtslOywtB1ELY2YMW3ZDzwo/TNUTC19UQqiAmt50y33eDPpEeUo5FIeJfEXzhqmrJe\n" +
                "NFz2uO8TQiTSDfsa92j0yjXfkkNXM+1wR1F6prbewL/5UxsagcLUKG/qGAo+V8VB1a7XYovxXRK7\n" +
                "swCoquXtqcKskqOmKWvf6QG4T4QQj/t+3+kB05R1zT+YWBm8zh67IGaOv5fCDvyPZSCJMNcp3Xna\n" +
                "GMNvSd5ZlKPc4WK4yS0XYxjnku4wXehmH4vtTdW824DXP1HTGkuIJXRr4Hj8TsrvLMpRrsHslI99\n" +
                "MJiEaQ7Thab1YhTAcI4kkpBjt4cRKeXbRrrCk/n8WIwVeV6LLUg5p9snD33H1N7ZjABPLSH/G7bG\n" +
                "GZiRJvYFI+0OAa/f1NbDDmz4rPZI8jX2ixn6zHSniLLsRmcVCZkz9fHFP6QbYnxw7b9gbPm3SFnm\n" +
                "6cbToa/gHBoPG1aVHYi1Bo23h+4y6M5mm08Yb6HDvsY9MT7IfxTt3ApFZ2GpEqkcOeysdkom3Xat\n" +
                "a3P7q4+NsnCWPY8X2XRns9wn1OiVzUcaEg5k80C1FhbV6CQV6uitwSiZlJIVhnXzIsVRoJJG3lQb\n" +
                "8Po9k2bXiJFXg9Oy/s6uIxLC3cq4eyPRq4B7sYcixUpFXCRkiwtm0JrXYnONGF0jRt4OQs30O5vZ\n" +
                "JBRlQpOsJNVmzu8IgXsJlBdLKtRh+0HFVeMiVinEKgV0QHWPGO9Nmgt4WVqZ9eApCXle9h4vQufF\n" +
                "Jjm2tuRAFYyOYqcoFE21iWWlc8XCSNXh6Q7VZNmNRjmahDO2UUrSZnVzRILAv/kpWYrXbllbvbyp\n" +
                "1j1ihObZ6avzzBEJ0lfkyZNiACQhHyIi6SpreuywlySpGMv5puQ+f5W0RvfYkjcEkpA/ZH5wWkKs\n" +
                "UuSIhQUV6oQNVMDr91lsXost4PXLm2oTk220ww1JCxj0KVIpEnidXLFw20iXZ9IMH4Ypbcsgj53n\n" +
                "JQE5fF7QtONu7FX2ayiKREq5prMlmRdhKEc73F6LzWuxsRWgc2gsaMRajDDWHQsKouaIBBCPoWTS\n" +
                "uGgpCbeheCbNnzWeTkascnPKFooEJXwNO/GZhFLacZebJhfJhAdyRIJY5nuGAiZ+eibNj+22SDvu\n" +
                "znQYonTsjfT6oS+74lvyTFnYRgxoKalQyw5UxSvLJRXqcsMR44EfoQhCOZoskmknJY5gTCDu77XY\n" +
                "wOZQG6VBSzxHLJzrOpfGXWxjTIwCWnotttC+NWCZ4fBxQYVaWqN77FDE+DjMVT/LFZSjia8hrvqC\n" +
                "5IqFCccAwVNi99WDiGVQHq9o59agtIRYpZDoVTF6VgmM16Nk0thfP2xbJ/YpDefQOCG9RTu3Fu3S\n" +
                "sUNByTQBiHGbSJUcLUA5msAa4vLtxCpFYpGGFd/SRE2rvLE293eE9ybNQUOtGbiu3QzNFpa11ceo\n" +
                "5Uoi9D6DSEmk+RbyptoYv1ToKX7n0FjoF3Fdu+m6dtPabpAdqMoRC1e8fnsSh/p4ftYWSfigJQFn\n" +
                "vSITJuEDny2GgdKO82NBkk9SoY7FWJXU7QireF0jRlNrz4pvCfzS0MhNUY0uUpXpIwxs3BPKh7C1\n" +
                "4My+Y0/FgVrOTBPHQ03iBX9PUXA8QoCDGKy9fzj060TqTv+IVoxQyzLddAaMVaQGvoSQx2YRc0SC\n" +
                "0nDeIAcZCG5YwVxz3lbn8JiE0JyHq2QUB57nim8ptF2FWKWQRz2SI1LKw362oEGfPqs9rHv22G5R\n" +
                "mq6W0NU5F3WKaGoYyNUA5sTOpiIJH3EYuDGGcD5orYxhlCWiCMci6J8fSsvQF88VC0vqdkQRuqEh\n" +
                "H8+kOd0TiwmHBWtQ/irgsf/J60O9EMLmzC3kxhiGNTLlhiNhD7/niATScHHRsPOuacfdsI2hIvXw\n" +
                "FSnlYcWwtcPAwdVOIN6bGDgeapJtJARFylmfZm7iBHbD5dCmZpRMqhs6EcpD2YGqUM3mmTRHUulh\n" +
                "La2kQh1q5EVKedgaA3v/MDdlopzVr8DVFiMJk1GkUTqOpRZSrvZmU1tPKFXEKkUoD8N6dFH6ka74\n" +
                "lmbC2bGgqGwkBnotttn0e4NcOoTMJk6hHE3GNHFmCXPFwnTPi2d0463WnrBuki3WEPIAABapSURB\n" +
                "VG7oBGO1JHpV6NJxDj2mf6FzaDz0EKC0RsfQOxIDQzu4pQ/pnnLBdgjhG6ElTHS/hGnjHB6iCR3b\n" +
                "kia4rt0Ma3PEKgUzjTC0iiXg9VvbH++wmUIYnisWwleDodlhrdCt1h7OLjVn1/lB6TZX9XFZSMJc\n" +
                "sRDChpxNEeBMkRJC5rrOhU0qMNMIQ1N87KP00bygKUvoFYOhv5HSkqa2Hg4ioowp5kwc8t8hJPxv\n" +
                "gw+Xj7ORrpwp0tWl3xup9rIk3DzD2Lvih0Y4KZk0UiwkyqimdECR/hnAwZaQ371z+E5CcAu5nKdT\n" +
                "cqCKyy8YhYdBmO0cjN1hi5S7X3MGRkq6pMsSTln4bwn5fpQJ9jAuD3HHWG+ZWh7emzQ/tguGrK4q\n" +
                "Vyy8N2l+rOfG9FCM/rSA13+rlTsVyniDnMVFYe8OOxwSSRgHmEa3Hg5nHZfUVaX1pF8onEPjXout\n" +
                "3HAkynJhn233TJoDXn9Q3LigQp0jFsa463stNlNbD/edY0JPLaYPcBJSwvs+jhlwqFdSoXYOjbtG\n" +
                "jJxdTXlTbYwhkBTCZ7Xf2NkKPdRiFAgJF53Mdg5yvMswJppLowShhCIO1W92+oTMRbzHoVuYKxbK\n" +
                "OQweMFjxLc0cfy99s7IZJ3BNGMixGWRmIfPfEmYACeEi+qx2LmcGyZtqY5xklHKkO26xVpZBoldx\n" +
                "yQdwCCV6Ff/7C2cACXPFQki2uq4as94YkkcL1gNev3NoLJlzJPAKQd5g1ptBZrXwX4uSTGn0VFSj\n" +
                "gwQ0l8RYE88wRyRg6v0DXv9Mh4Fp7gKBmRjjLtAm1DViZALLTOKxoELNvRzl2AwGvH6I+nJWmrMO\n" +
                "SLhLN3P8Pde1m+yWShwYw7K2+lj6VqSW+cwXnKhpZTIl0NyFWdC5YmHYszk+i21p1Rd61A/s9Vps\n" +
                "UC4TY0+N1ELT1cLl27lHjITb0pzsJyElk4qUcp/V7jw/xrExdAyNcRbHp2SFjGaLUqgN/Ik3v2c3\n" +
                "XJavTozRdLVcr/hj7i5juB42HGhRzsrEs98nBECtkyPNkcNQKGPoAZMyc8HK19tirlCLHUzvJkom\n" +
                "LW19mbOdpZRbbzCztGgmkRBihhzHSEG8yTkZyyxv3MN4TXQ4SZk82BGasrZ6btqubO5o5Dg+6Tw/\n" +
                "RjjPSa4LEjKl1fY0mIjoKG2rT3f7mSBzkaY4MMz2CGt40+XM79zKfXzSbhgmEdoZIwmTBZRWR+mH\n" +
                "mT7+p3u9lrXVs81F+lQ32xiKVYooPaBSsrNs4TYeQ1YncHBcJr6OSAil1Su+JSfnnqGkQp1WJ4ot\n" +
                "nNKkRR/Y2BFjpPdNh4vLfaIc9i8uy8TXFwnJarKLe2MIxip9B7TZjm5au7zQjrtsHqavk11p68vc\n" +
                "14sFvH44liVvrM2gVf2djo6ODPq4YpVi/m8+oB13CyrUnI0TeWiKd+l+/f7V3/4mkPJXvvepOU+a\n" +
                "v+L12w3DdwaupfVb3B2f/u2/B75FiGNo7M4/pOW9inZuVZ/5AffLY/6vP/BMWSR61e/96b4MWtXf\n" +
                "+uabbzLLGMIh1LSOoY4Cr8XGnlWECEWkRlIcmMHr+tdWfEthO4OgHE2xLCQReopxY4qVxxuRaZGQ\n" +
                "IxKUG47ExUCPezElb+0eMa74lihZYWYxMCNJSMmkkKvgpkNmWL9U03kI+RaWgbqhE3EFe66cHW1v\n" +
                "/mlKeAjrgeMy8XVKQrLaEnetjCHwcHP7q8i6UAbG1c1l1jz/8eCoRqeUSPOTfHeo8stEM5ipJIQS\n" +
                "5JQYQ4dt4WL/5fbmt/tODcS7EaQ1yZb1DKT9ywPdFwgh99yLDttCSsxgZgVFGWRYdJSBYKPUOTTu\n" +
                "s9rDTmuIxQ+5cnb03N/+0ycX/9l++w7tX3Y575ZtURTEsyUX1ehWvP77n99GBsbLQELIpf99debz\n" +
                "2VK14v/evjNx9SYhpGzLpoTNoHNonJIV/qe/+TMkIYee4UYp1EYEvP54C6Mu9l/++c/OAfc0OuVm\n" +
                "bZn99p1StWL3wRfi/RiF27WCjVLXyE1kYLx/OHH15mZt2fd/eJAS5tm/uDPz+eysef6ZLZsoIRXX\n" +
                "6wS8/v/T8OPf/iag7GjkeWvDbCMhIaTgebXdcNlntcebM1Q8+7TLcXfv/6ix3/51ILByyzgjkW44\n" +
                "fPK13CcSOdglVinWLQ8pWeF/GWiPd+k7bAv2L+7sPlit1D4Dt6P89zUO28Kc2eZx3y//riauV5v/\n" +
                "6w+++uW/ULLCNUlZrXcS5oqFUOFF33HHVa2b+0RO+Xc1RbJCgZCaGLlJCfL+pOP7IEQdtoVfXPwl\n" +
                "LI64eFhUo1v48FfpyOPzFiKlvOLS24L4Syb+/meDn3zwz5QwT/Hs0w/ILKT01eUF0vxtL26NyxLS\n" +
                "Dreptee3vwloOlueKpVl6JX8dkavg80djTkigWfKkkA1Ke1fHh+eIITsa9ojUxQTQkxT1p63+q4P\n" +
                "fzp+aSIBe7jtWhefZzKnFiV1OyINlnksXjm8v0RR/IHhIwjMMNBVa+MNk860G1Z8S9DsOHMvZmaT\n" +
                "kGnHZG03JNANifbT22uf11VrCSHG0em+0wP00jIh5Dm9MiFtJtUNncj6kGmOSKDpPJSY9rvYf7n7\n" +
                "aN+/TlkbDu/X6JTGsekzr/fS/uXEPgkz1ntzR2aXT2Re2Voorle8Rjvuyhv3xHszPO5F2HoHui8Y\n" +
                "x6ZLFMVO24KuSruvaU/30b6Gw/vBQsYL14jR1NqTlaVtIqVc09mSWPzD415sb/4p80+JdIPHfR9+\n" +
                "aH7rlQQudcL3HS1h6gG7ctgx1NEBDOw7NQAM1FVpCSEanfLM6z1O24JpyprY5ymq0W271sXzmXgJ\n" +
                "oLT15W0jXQkzUCLN//N3DlGCPEKIrkqrqyovURQTQuivE7GEs52DcG6wNANLZLLQEpLVqm6RUr5t\n" +
                "pCvevwXPpOHw/vbmtykh5XEt0kvLDS37QaYmA+fQmLXdkAUmMRkDCFJ/oOdC85EGjV7pcS++e2qA\n" +
                "URy0n17yL8drBr0W28SuNkKItv/NjPYGs4qETAV9aevLiVUPwkIhhFCCvJZTzYkJ0bAfbK5z0G64\n" +
                "nLkeYIzjMaJf2BJF8eGTzZQwjxBC+5cv9l8G9fHmO4lU4d6oafVZ7UU7t2oNR7Jg9WaDHIUIDXS2\n" +
                "nOs6l9gwQ5PRSggpURSnkIHwwTZ3NFZe7cxEdSpv3LN96t1UMdA0ZYUwDCXMazi8/8X66sSu82zn\n" +
                "oM9qzxEJuO+dgZbw8ZhuPO26dpOSFVbGHz2HUsaGw/tht04HPJPm2c5BjrvuJoaSuh1lbfXJN784\n" +
                "83qv07YA/vYHho90VdqGw/uZeFgCyDIhmoUkDHj90LV6rY78xkhFx9AYl8Nx4xKfsgNVTI/gpLz0\n" +
                "KSvUgnYf7XPaFiAY03B4P9s/TPj+Zo0QBWRwxUyYL/PkE2KVAgq7BRulKa8k9LgXZ2/ZipJrf0ht\n" +
                "lBbV6KDE5+s5B0+KbERK+X888orqzA+KalLQIunK2dFzf/eh7Ytfb9ulK//uc9bp2777XxNCAr9Z\n" +
                "Ofd3H5YoincffCGBIsF//ZO/uv/5LCUr/M8D7d958gkkIU8BRaSeKcu9T82F27VPJn1QjY2L/R9d\n" +
                "en/E417UJJTND/IVC7drf+9P94lVim8/mcv9xNwHl0tWKKurUp3+n2Vt9WKVIvmVTfuXVwIr9i/u\n" +
                "QCGox71Y/l0N8NBpW5j5fJYdoYkL9v5h+3uXCSG68ycEG6XZtGizSo4+jAfUHfNMWRJzDqOjvflt\n" +
                "j/t+qVrx2luvpNB7DHj9nkmza8ToumrkIKUhUsoLKtQldVWpFQuQfvC4Fvc17bnYf5kQwiR7aP9y\n" +
                "99E+QkhiDGRcwc3tr67VyDokYYLOg0Sv0g2dSJ0lvHx9+FMo9QAPJx0f3muxeSbNnkmz12qLNBMm\n" +
                "YeKJVQpJhTpN7UYdtoW/fL33YXRHUUwIcdoWWk42lak3QXlaAgykHe4bO1tXfEt8dvWRhOGXMrRF\n" +
                "S76sCaLqEFGgBHnH+9+4514skObDg8nn9KPvJj6LzWuxgamE7/VYU0nJCimZFManCTZKKZk0rS1A\n" +
                "af/ylbOfSKT5O/ZWMldJUpRPCHntrYYzh3sJIW92H0osIhrw+o11x3xW+1o1cUMSJgXn0JiprZcQ\n" +
                "kkwPPI978czhXl211jg6TS8t//k7h5js1viliQ8MH5UoihOuMk1+o1lZLVunNkrXZP7JrHl+oPuC\n" +
                "x33/xfrq3QerGb2g0Skh3+OwLRhHP9t98IXE1DuknXJEgm3XujJlwMt6D8ywAQ6PZ8riGrkpVikS\n" +
                "PW/2LY97cWLk5kpg5XuNLzEHLDzuxZ//dDA3N4d8i0x8fHNz+TPifBHHX/BJaT61UQr/rYmJGL80\n" +
                "8fOfnaP9y5Qg7/s/PAgBT6X2GadtwWS0FpUUyjYVi/NFSu0ziR2YNrX1LAxPEEIqLv1l5h4XXNck\n" +
                "JIQUVKjh4O/d8enEgqW5T+Ro9EpKmDfz+az3/tdK7TNw6vTdUwNu51fbdm39/g8PGsc+s39xR19d\n" +
                "TtYZ3I6vCCHyZzfab99xOe4yh+KV2mes07enf2Uq/31NvO0q2EIGxnprOg8Vbtdm8WXMchISmHc/\n" +
                "afbPf7nw4a8STloonn0aBGf5d58DCzAxcpMQYr99x/bFr522/yeR5uury2fN89cvTSiefTqxjT9T\n" +
                "YJqyBgIr4nyRbFNx+Xc1Gr3SZLTOmW0F0nzZpmKPexGsn/b3n0s4p8q4EpvbX336lV3ZvUSz2ScM\n" +
                "de4T7koU6iVCsOHK2VHj2LSuSrt9b6VMUdx9tG/ObNPolM1vNWTfZYQADPjGhBB21QvERSlBXsmm\n" +
                "Ytq/nFhZdigDszUcGoRvk3WAXLFQN3RCpJSv+JaMdce8yY0imr1lo5eWqafy7rkXy9SbCCFl6k0y\n" +
                "RbFxdHrObCOElCiKB7ovvHHwxwmfSOQnuo/2XR/+tGzLpuYjDYQQaA4CkCmKNTolvbTsnF+AY5nI\n" +
                "QCRhGnmoq9a2nGwihPQc7YfTTyWbigkhVwY/gROrJqPVODZNLy3DyYyMBjRovXJ2lBCyo7aSELLk\n" +
                "pxniMU8zjk6bjNZSteLN7kM79lYiA1GOcqRLZ83zkCcsU2+C/JiuSmscm4bfUoK8hJNjfIDDtnD9\n" +
                "0gR8HUZgw6kI5jmlasUrh/dLpPm0f9k0ZU0yZbo+GbjuSJhyHjLOUnvT29RTeYdPNr/ffUEgpExG\n" +
                "K5M3y1C8cfDH9NKyRqfc1/QSRDgpYd6seb7naD8lyGs+2nD90qdg6qEgJsm3W7cMXEdyNE3+4arR\n" +
                "+JIQsrv+BYk0//DJZoftS0qQl4wqWxOAYb9ydhTqy8CsSaQbrpwdfeO//RhqQcvUm0rVCnpp2eO6\n" +
                "3/xWQ/ORhuYjDckzcLZzcN0ycD1awiB7SJKrpwkF6NLvNb4UhYQwhYYQ4pxf0OiV+5r2pO8kcSwY\n" +
                "vzTx8dlRiHky4pPdHE1Xpd19sBqkNTwukW443vdGSt4d+gMRQrKgbxpawrjtIfQINbX1pnDU4T33\n" +
                "okS6IQoDaf9y36n358w22r+s0SuNY9P3Vqfz0f7lWfN8wn0440XfqYH25rcJIQIhRS8t66q0vR+e\n" +
                "KlEUg8iUSPMhztnQsr/h8P577kXYOCTS/OYjDW++kwJ7BVshMFDTeWh9MpAQkkPWK3LFQlA+zqHx\n" +
                "ua5ztMO9uaMx+eKv3Qcf4wqapqwe932mx5FGp4QwIxPzKFEUv/ZWA5idWfN8gTQ/yeiOw7ZA+2mP\n" +
                "676uWgsdVhta9pdtUZiM1u21zxNCNHol6XkgRz2uRYjxgiI1jk2PD09MjX02Z7aBxpZI85M/TkkI\n" +
                "8VpsprYecM6Vxxszca4gkjA10HS2FFSoTW29zqFxr8VWbjiS7irhVQfyAVFhQRtHp9m9WN7vvnD4\n" +
                "ZDPtX+452k8IoQR5bNUKCYMCab6kaAP4aZHeC04/Mv/UVWv3Ne1x2BYGei6UqhWEEF11OURcIK4L\n" +
                "j7+yekQLPMA5s00i3fC9xpf01eWpks1Mf+QUhseQhBmMkroqSib9rPG0z2q/sbNV09WS1g5CwArq\n" +
                "qUdWMyjA3fXVGr3yA8NHbLqWqhUe9yKkCuAE48eDo+y/7f3wVJT3ogR5DYf3U0/lAVcpYd7hk81Q\n" +
                "2UMJ8phcHzSlDz0kuftgNZjQVEaAOgehKDSLTyehTxg3JBVqGOey4luabjoz02FI33uBo3Wx/6NZ\n" +
                "8/yseZ7NN+iN+9B7/HoZzBGEQNj1N6VqRe+Hp97+x7+AsoFIgEimw7ZgHJ2Gg+3Aw31NLxFC6KVl\n" +
                "ZiSLRq+kBHmhJT5l6k0pZCA4gcDAZEbKIAmzE5RMum2kS964hxBiN1y+UdNKO9xpEcB6ZfORBo9r\n" +
                "sedov3F0ms03QsjU6DRZLUaBIdJlW8JINef8QvfRvndPve9x3Y/yXgIhBZaTYTvjlxJCJNINxrHp\n" +
                "R3j4VF7yk6ujSNDr+tc8U5ZkRsqgHM1+bO5olFSoTa09IE2TbD4dhYdvbjoEx/MZb+368KdnXu/1\n" +
                "uBYJIRBcBatICSngDBTHxae0FcUmozU0mW4yWiXSDW++08J0wt6xtzJ9mRJ2G/IkO+ojCdcFYJyL\n" +
                "qbXHM2WZOf6ea8So6WpJebRG8mjMc1/THok03zg2rdErNTol/ApSF8bRz8Bj1OiUDDM1emUsHW6A\n" +
                "5FfOjsoUVodtQaYohsAMHISHTtgM99LEQM+k2dTWA81y1m0mMDrWabI+Ftj7h2c7ByGClyaTGB2Q\n" +
                "TmA8SWAdFI5JpBuAqGXqTVEyIvBkQggcMoInJ9xwKQEDONNhgDQgjLNOa6sbJGF2AqYxQ+N6kVKu\n" +
                "7Ghc82XEVNsAwLhFef6seT75srIEwJ5IJW/cU9pWjzEYJGEKTCIhpKRuR0py+lkM9sgNnuxcSMJs\n" +
                "AFtZ5YgE8qZaeVMtUjFUOMx2DrKvUlnmT/BEEvJ3j8diq0ibFOoFJCEX3g7MaoZ4Q1lb/XqmIu1w\n" +
                "O86P2fuHQa5L9KrNHY2YgUASrgEVS+qq1ptAZYtPoF9ZWz26f0jCtaRiCof78V+W2/uHXdduIv2Q\n" +
                "hHykIiGkaOfWkgNVWTNHlu34Oc+P2Q3D7G8qb6pF+iEJeWofKFlhUY0u5ePH1gSuEaNrxMgozxyR\n" +
                "oGiXLiXztBFIwnQFKpxDY4y5ECnlsrqqol26jFuyMNmbPTJRpJQrmmqlqZjmi0AScm09GNtYVKPj\n" +
                "s34LO640m6w6knDdIeD1u0eMrqtGRqaCnCuoUEsq1JIKNU+WtWfSfG/S7Jk0QxaULTth48BbiSTM\n" +
                "HtvomTSz5+/miAQwPVesUohVCs4kq9di81ltXovt3qQZus4xSNMwbQSSkEdgBmLfmzQHjd0FTsLE\n" +
                "z4IKNSEkee1KO9z0HTftcC/dcfsstiWHO4h1IDglFeqCCjX6e0jCdQfa4fZMmr0Wm9diY0vBUJIw\n" +
                "RjI6LVe8fqajcZTZ2mwLLKlQI/GQhIhHrNa9STNwKeD1h1qtBCDRq4C9uWKhWKUQqRTIOiQhIm4F\n" +
                "C+PpQVVGf3LBqqlcq0H2CCQhApFJwG5rCASSEIFAEiIQCCQhAoEkRCAQSEIEAkmIQCCQhAgEkhCB\n" +
                "QCAJEQgkIQKBQBIiEEhCBAKBJEQgkIQIBAJJiEAgCREIBJIQgUASIhAIJCECgSREIBBIQgQCSYhA\n" +
                "IJCECASSEIFAIAkRCCQhAoFAEiIQSEIEAoEkRCCQhAgEAkmIQCAJEQgEkhCBQBIiEAgkIQKBJEQg\n" +
                "EEhCBAJJiEAgkIQIBJIQgUAgCREIJCECgUASIhBIQgQCgSREIDID/x/SIJFenwIhyAAAAABJRU5E\n" +
                "rkJggg==\"}", ArticleModel.class));
        articlesDefault.add(gson.fromJson("{\"page\":2,\"link\":\"https://developer.ibm.com/code/patterns/use-mongoose-and-mongodb-to-serve-app-data/\",\"title\":\"cloud\",\"subtitle\":\"native\",\"image\":\"cloud\",\"subtext\":\"\",\"description\":\"this app was architected to scale on cloud technology\",\"imageEncoded\":\"iVBORw0KGgoAAAANSUhEUgAAASwAAAEsCAIAAAD2HxkiAAAACXBIWXMAAAsSAAALEgHS3X78AAAQ\n" +
                "SElEQVR42u3dP0xb96LA8dP7OviYN4Cj58o1y7FgsRxfyRlsXd5AOF2gCcODASR3KnS7cceKLF2I\n" +
                "OtbcrZApSGSADoTAUkOGx5U9xINjsTiyF7vWsxSTocaWbiXe4ApxSXp+x3+wz++c72eK1CQl9u97\n" +
                "zu/8+51PLi8vFQDD8xc+AoAIASIEQIQAEQIgQoAIARAhQIQAiBAgQgBECBAhACIEiBAAEQJECIAI\n" +
                "ASIEQIQAEQIgQoAIARAhQIQAiBAgQgBECBAhACIEiBAAEQJECIAIASIEQIQAEQIgQoAIARAhQIQA\n" +
                "iBAgQgBECBAhACIEiBAAEQJECIAIASIEQIQAEQIgQoAIARAhQIQAiBAgQgBECBAhACIEiBAAEQJE\n" +
                "CBAhACIEiBAAEQJECIAIASIEQIQAEQIgQsBJPuUjsKVCvvjHL96UbvynO94xz2ejiqKoI+q45uOz\n" +
                "IkL0J7lysVopVd/Vzt/mSx39WY931OMdG9d849rn/oCPLAfvk8vLSz4FScPLpc8K+VKlVO3jX6u6\n" +
                "XZN3AxMh7a+xoMc7xudMhLgplz7LZc5y6bPmReu2/18e72g4Gozq99g9EiGUeu38ZP80k8oOoL0P\n" +
                "+TXf/YdT4VhQHXHxXRChE6edhzupTo/0boPqdoVjwbllnWkqEZLfkEVnIqRIhORniRQXVh4wQSVC\n" +
                "u2k2WntbB5njrBQ/rep2Tc9PzS3rfHFEaBMn+6dHO6mhnHrphV/zxROLnEElQrnVa+fPkru3Mf9U\n" +
                "3S5/4N/yuKVZ7uySzi6RCGWVS59tJ3d73wF6vKOToYDHOzZ5VxPektZstMqlX5u/tcqlaiFfrBSr\n" +
                "vf8Afs33zVqcEzZEKJm9rYNXL/7ZY3jhaHDybqDHcyTlUrXwpvg2X8plznrZ8cYTi+FYkG+WCCXQ\n" +
                "bLR+evKsu8mh6nZF9cgt3cvSbLT+uC+n2xqZmhKhBMql6nZyt4vbPidCWmzmXlSPDOZINZ3KZo5f\n" +
                "12vvO/2z0ZlIPLHIF02E1i1wY22z02OwiZA2t6xPhgKD/4Ezqezh8186TdGv+RLrq1xIJEI7FDjE\n" +
                "/G6kuLd10NFPTodEKH2Bqtu1sPJgMJNPk4eLhzu/dHQmiQ6JUOICw9FgPLFoweHb6QEtHRKhJdRr\n" +
                "5z8k/mGyQClO9Hd0cWUipCXWVxkGRDjMWVzy8abJXYdEl7w7us2A86VEOEzJx5smrwdK92hCR1PT\n" +
                "//n6y/vzU4yHG1jycBDTNpMFzi7p1jwINDCu+RLrq35z9wz8/PTl1TJwIMLBTdhMHjjFHy1KepeJ\n" +
                "OuL67se/R2dMncLdXN9uNloMDCIc3KHgdnLXZIHWuQ7RnXhi0UyHzYvWT0+eMTaIcEBMnrSwQYEd\n" +
                "dfg2XzrZP2V4EOEgJqJmboCefvg3exTYtrDywMzx4dFOql47Z5AQ4fAnou1zoXb6h6sjrsT6qsc7\n" +
                "KpyUPjM3USdCdOlw5xfhRNSv+WxW4FWHq2tfqW6XcFKaS58xVIjwVpRLVeEZUdXt+mYtbtc7ucbN\n" +
                "bV/2nh4wWojwVuxticdWPLFo72UgonokHBXcdlevvecMDRH2XyFfFF6aD0eDTlgAIp5YFB4cHu2k\n" +
                "uGxIhH0/GkwJJ6IOuYVSHXEtfC2YlDYvWuwMiXDQu8HZZd05D/WEY0HhpPQVEVJOH2VSgpWzPd5R\n" +
                "p93BvLDypXBnKPzciBCm1GvnwuXrHfgsj8c7Nv3wb8a/5+TFKRGiD9KizflESBv6OjFDMbf8hfFl\n" +
                "w0qpWu7r+4aJ0Klz0ePXorHo0KU41RGX8Gywk48MibA/yqWq8YqA7aWyHfv5CDdATr57hgj7tBtM\n" +
                "iXaDS184+fPxeMeMT5M2L1qO7ZAI+8P4gYn2i6Yd/hEJn3Lq5R0YRMhcVDAXDceCLPgXjgWNb6Bh\n" +
                "T4juFd4I1k0RXrB2SoeiGakzz5ESIXPRge4Me9ycESE+rlI02n5P3g3wEf3xUYQCxhcMb+ktwkRo\n" +
                "/wNC4+d3mYua3ySxJ0T/d4OKokze1fiUrkyENOPDQgeuPfMpw+LmOGi0yqVf6//3/l3tvNlofvRU\n" +
                "gXtEbS9nNK75jFezVd0u3uHe0eT8Xe3caZ8YESqKohTyxcKbUiFfrBSrJt+sYPKilj/g4+O9Tvh+\n" +
                "78KbktNuLXJuhL2/md3kqQjC+3BGanACptloMh21v1z6LHOcHcz9GXeYi35sMm/wXx14qdBBETYb\n" +
                "rZP908zx607fvd4Lz2ejVHdziq75DLaAlWL1ZP80pt9zzj1Gnzonv1f7px29b53p6C0xrqt50fr5\n" +
                "6cujndT0/NT9+SknpGj/CA93UkPJT1EU4VpjzjRu4mRV86J19Dz1av90dlm3/YIgdo6wkC9uJ3cH\n" +
                "Ofn8IEIOCHubwly0fn768tWL03hi0cZzCntG2H4VxNAfjWEu2pePpV57v/F4S7p3GDs6wkK+uLm+\n" +
                "PZT55w2cGu2jzHG2kC/acpdotwgPd1JHz1N9+av8mk8dcd3xjn04q6yUqheNpvDKPqdGDY6WuzhM\n" +
                "aO8SZ5d0m63WY58Ie5+CtpeBmQwF/AHfuGb2TpdCvti+x61d5vXL0ExH/8z0w6lXL067O1w/ep6q\n" +
                "lKrxxKJtpqafXF5e2uCfUa+d//Rku9LVdV6PdzQcDUb1e+bDE/4w72rn5WLVaev8djzDTGXTx6+7\n" +
                "e3zJr/kS66v26NAOEZZL1Y21zS4OAqMzkageYX819K/v1f6pcN3kD6lu16Mnq/3adBLhQAtU3a7p\n" +
                "+amYHuESgqXmMulUttMruvboUO4IuygwOhOZW9bJz7IH9ntbBx3tFW3QocQRdlqgX/PFE4s2mL04\n" +
                "YYK6ndw1f4Qve4eyRthstJKPN81/T/Y7r217HV1tkvo8jawR/vDtP0wW6PGOrq59xQ5Q0l3i5pNn\n" +
                "Jq9k+DXfdz/+XcZ/ppRrzJifq0yEtO9+fESBkhrXfN/9+Mh4WZorlVJ1b+uACAeh/Uiumd8ZnYnY\n" +
                "5lKSY6kjrsT6qnAJ/bZXL/4p4zLekkVYr51vJ3dNFujAN3LaVTyxaLLD7eSudOu1SRbhs+SumdOh\n" +
                "FOjYDpsXrWfmNtNE2I2T/VMztzhRoMM7fJsvnUj1ylFpImw2Wkc74hPW4WiQAunwaCfVbLSIsM/2\n" +
                "tg6EE9H25XhGqr0trDzwi053Ny9aEp0plSPCeu1ceEZUdbvs9HgL/vSLHnF9sxY3frGMoiiZ46ws\n" +
                "Z2jkiPDQxER0dlnneqBDeLxjCysP+jJsiLBvu8FwNMjDe44S1SPC113JsjOUIELh9kx1uxZWvmRc\n" +
                "Ou/g8EvhpFSKnaHVIzSzG5yen+LRJGdOSqdF0x8pdoZWjzCdyoq+iVEej3CsuWVduMKycAgRoSLa\n" +
                "kr0WfA1LXzAWHd2haAAIhxARGsmlz4wfY/F4R6N6hIHoZFE9YrwzrNfeW/yubmtHKFq/cPohZ0Qh\n" +
                "HgZDX4td7j2h8W+I6fcYgojp94xPk7In7FK5JFjfOjoT4f4YKIqijrjCMaNrhs2LlpXfPWrdCIVb\n" +
                "L+G1WjiHcDBYeWdo3QgL+aLRxs8t2PjBWRHGgsYzUuPhRIQfZ/zoIAWioyHR3WL7jo5QOINn7Xp0\n" +
                "OiQse1ho0QgrRVGEdzWGHToaEsJBRYT/5p3h/X6q28XNorjB4x0zPix8Z9WbSC0aofFhtD/Ac4Po\n" +
                "eGBY9tyMlIv/8vAu7HSmQMpjQnVEZcChv4OKCG8yvleGszLoYmB08RpZpqOAIxAhQIQAEUqHEzMg\n" +
                "wiFrNpp8cyBCAEQIEOHQpqO/tfjmYJuBYdEIjdfPsvJSBRgi44EhXKGUCG98XkYPSUj3PmQMhvHA\n" +
                "sOyTNxaN8I7h5/WOCNH5wLhDhH3cE1p5qQIMkfHAYE/YGeHDSlZetwdDIRwSln0CzqoRih7bLRc5\n" +
                "N4POhsR4gAg7nI4an8uy+MLmGDzjIeHxjjId7WJG+rnx7L/Z4Goh/tBstIwPCI2HExF+nNRrKmPQ\n" +
                "u0GZ12u3boTCx+fTln/vHAZGOBisvBqDdSP0eMf8hqez3uZLXLWHoij12rnxXNSv+ay8Rqal7x2N\n" +
                "zgheAHq4k2IIQjgMhAOJCP/UX0UvnMgcZzk943DNRitznO1xIBGh0Yx0IiSYyp/snzIQnUw4ACZC\n" +
                "msXXa7f6o0yxGcG7eI+epzgydPLR4NHzVI9DiAhFh4V6xPgFA4qi7G29ZDg6k/CrV92uqB4hwl5N\n" +
                "z08Z/4Zc5oxbSR2okC8Kb5wSDh4iNOX+/JRwZ7id3OUMjaM0G63t5K5wN3ifCPtCHXEJt2f12vu9\n" +
                "rQOGppMmogf12nvhblAdcRHh4HaGmeNsJpVldDpBJpUVXpaQZTcoTYTqiGt2WTezdWT5Gdsrl6pm\n" +
                "Zj2zy7oUu0FFotXW7s9P+UUPZTYvWhtrmxwc2vtQcGNtU/h+Jb/mk2U3qMi15GE8sSj+ki5aycd0\n" +
                "aNsCk483zbzhzMxQIcJujGu+2SXxpLRSqtKhXQusmDjcmF3S5XqXs2SL/84t634Tny8dOrZAv+ab\n" +
                "M3H6gAh7nZQKz5S2O/zh2w3O09hAuVT94dsNMwWqbpdcE1FZIxzXfCY/6Hrt/cbaJg/gSy2XPttY\n" +
                "2xReErzaQMs1EW37j++//166H/qz8f9SzK0++vu/fs/+b05RlMm7AQa0dPa2Dn5++vL3f/1u5jfP\n" +
                "Lun/PRuV8Z/5yeXlpaTf0HZyV3jF9vqhgqSbScdOQbeTuxXTRxPRmYiME1HpI1QUJfl4s6PVuGeX\n" +
                "9PuS3MrkWM1G62T/VPiA0nUTIS2xvirvP1nuCM2fNLt+7L6w8sD6j7c4UyaV3ds6MHMl8PocJ7G+\n" +
                "KvWGVe4Iu+tQURSPd3Ru6YtwLMhe0SJfYi59dvj8F5MnYOxUoB0i7OL48PpeMapHovo9jhWHeOyX\n" +
                "Sb3OpLId7f1scBxowwi77vBqgzoZ0qhxwO3lMmed7vrsV6CtImwfUWxv7PbyN6hulz/gmwwFxjWf\n" +
                "+p+uyRAXNvqjkC82f2uVS9VCvlgpVrvY710Xf7Rop6N6W0XY3sRuPnnW9fb1zw4gLb5cl2XVa+d9\n" +
                "/y5W176y2YTFbhG2j/L3tg66nprCsqIzkYWVB/Y7l2bDCNty6bO9pwf93QxjWDze0YWvH4StvYYv\n" +
                "EX58l9jpZV9YkO1vsbBzhFeHJYc7KWanks4/55Z12x+Q2z9CUiQ/IrRWiif7p91dGsYAtG+fuD8/\n" +
                "5ajT0c6K8Eomlc1lznjxvXWEo8FwNOjMe3odGmFb+5bFQr6YS5+xbxzKfi8cC06GAg6/idfREV5X\n" +
                "LlUrxWq59Gv7FzR5S9X5A75xzTeufd7+BZ8JERq5eslM4U2JT6NrVy+L5x5AIgQs6i98BAARAkQI\n" +
                "gAgBIgRAhAARAiBCgAgBECFAhACIECBCAEQIECEAIgSIEAARAkQIgAgBIgRAhAARAiBCgAgBECFA\n" +
                "hACIECBCAEQIECEAIgSIEAARAkQIgAgBIgRAhAARAiBCgAgBECFAhACIECBCAEQIECEAIgSIEAAR\n" +
                "AkQIgAgBIgRAhAARAiBCgAgBECFAhACIECBCAEQIECEAIgSIECBCAEQIECEAIgSIEAARAkQIgAgB\n" +
                "IgRAhICT/D//Blo+UjvsVQAAAABJRU5ErkJggg==\n" +
                "\"}",ArticleModel.class));
        articlesDefault.add(gson.fromJson("{\"page\":3,\"link\":\"https://developer.ibm.com/code/labs/Containers-Running-Your-First-Container\",\"title\":\"accelerated\",\"subtitle\":\"devops\",\"image\":\"Run\",\"subtext\":\"\",\"description\":\"we built this quickly with a small team and some smart CI/CD\",\"imageEncoded\":\"iVBORw0KGgoAAAANSUhEUgAAASwAAAEsCAIAAAD2HxkiAAAACXBIWXMAAAsSAAALEgHS3X78AAAV\n" +
                "6UlEQVR42u2dUWhbV5qA1U4ySPK02A6joMoLe4U1C1pFBedBJl4Y28o8xE3yUPshBuVhqNW3Wn1p\n" +
                "txPDJlmwp5OZh9h9GKhddtkI7AW7DE7i7ENkm2EdrIcIVtHqYVWkhZVHVDBymI4iQQLZB7VuqM+9\n" +
                "lnXvle6Vvu/RCbJ87vnu+c85//nPay9fvrQAQOt4nSYAQEIAJAQAJARAQgBAQgAkBAAkBEBCAEBC\n" +
                "ACQEACQEQEIAQEIAJAQAJARAQgBAQgAkBAAkBEBCAEBCACQEACQEQEIAQEIAJAQAJARAQgBAQgAk\n" +
                "BAAkBEBCAEBCACQEACQEQEIAQEIAJAQAJARAQgBAQgAkBAAkBEBCAEBCACQEACQEQEIAQEIAJAQA\n" +
                "JARAQgBAQgAkBAAkBEBCAEBCACQEACQEQEIAQEIAJAQAJARAQgAkBAAkBEBCAEBCACQEACQEQEIA\n" +
                "QEIAJAQAJARAQgBAQgAkBAAkBEBCAEBCACQEAF05QROoJJPKWiyWzJOcxWKplCv5XIE2aQL2LptL\n" +
                "cvZJTs8Zt63Lauq/5bWXL1/yROunUq5mnmTzuUImlS0V90vFp7RJy/EHvP6ANxAcQMI2Fy8ZT2dS\n" +
                "WawzLDa7dfjy0NhkEAnbyr3kbjoZTyfjaVrDLPQ6ukORCY/PjYTmJrmbjm8mcM+8XLgSNNGQiITf\n" +
                "Uyru78YS2+s7lWdVWsPsBEYHQpEJU3xVVke/1W9jORbfTNAUbUPtaZrCwxPop1I/l+S0dVlrk5A+\n" +
                "yWn7idJyeW0nYy9XeFaufJXKqVyE8JyR2uARbCzHlJvCZrf6B70en7v3dPf3D+7rp5lUNrmbVghb\n" +
                "4psJW5d1fOoi4ahBqZSra0v3GtCv19Ht8bk9PrfL7eyTnCpfAflsobbh0YCT/T7pamSi19Fj6un3\n" +
                "4q+jCk09duW88t5DPJbYWHmosGod/lXIP+hFQiO+fY8197PZrZ4zbn/A6zkj6dfpk7vpTCqbjKeP\n" +
                "tREyfOnc2OR5M25YV8rV61O35J7CsRZXNpZjD1Zics/u5tLHRm6fjpMwk8quLd3fqzuvJTA64A94\n" +
                "m/wqzecK8djj+m202a2hyITB3/eHic6vCiORxv6c5G46Or8qVNrgizQdJGGlXN1Yfrh991GdMefY\n" +
                "lfP+QW9r36DH2izxB7yhyIRZhsRScf96+LfCf5qenWpsoy+Tyi7MLAn/6ebiR4aN2ztFwnyuEJ1f\n" +
                "rWcA7PdJY5NBQ+32lor7W+s78VjiyPjZREOi3DD47nvvjFweavhjt9Z3vvzivrkGw46QUO7BGF+/\n" +
                "H4zkW+s79Uxlhy+dM/6S4MeT/3z4D+n3SZHZsMpPnp9ZPLzKZbNbby3/kzGb4kc3btxo7xD033//\n" +
                "h4df/vFI/UKRibHJ86cMvNJ48scnPGfc/3Ah8Jf9b5SH9P/9n/9LxtPegZ/ZumzG/FuSu+n4lmAY\n" +
                "DEUm1D+CU6d7Do+xL56/6JOcp/t+asDWaOfzhKXi/vzMovImRK+jOzQ9EZkNmyXb0NZlDUUmbi5+\n" +
                "1O9T2iTcyxU+jXxWO2ZlQIRfrN8nafIUPD63sHEM2xptK2E+V/g08pnyiDF86dwnt6fNeASm19ET\n" +
                "mQ2HpidsdtllmMqz6sLMUjyWMObTOfzDwdGzWn2+8KMMe9SzPTNmFFarvx8AzZZrL1hsCA74B73R\n" +
                "+VWF5dPowuqfi/tGy2YWZiZomAAk/Cg1KUpIeDzisUR0YVWp744OjE9dNPtx7IPoNHwtFI8l1pbu\n" +
                "yb10HqzESsV942dRariFYK4sohMdZaDNbh2fumjeI9gKQ6LL7VTYgzF+NrPyFLexDzTs0NfOc0Jl\n" +
                "A3sd3dNz4fYzsEaf5IzMhv0B2R3C+GYiOr9qhK9aKu5boC0lVDbQJTk/uT2tMt/aFKHp8KVzBvdQ\n" +
                "GCtWyhqf4dT8A5HwCPK5goKBgdGByGy4PSaBRzI+dTE0PaHg4db6jgG/9p7WS5d75il793p7GLhw\n" +
                "bVHBQBNlVGo1RVTw8Msv7rd830K4s6LhFoLwoxS2c5BQbdShsBthohoHzfQwurDa2k0zl9spmlA8\n" +
                "1m5u8rjOX4qEGvD53B25wKNjDazHw4Vriy2cNQl3aDWsrCX8KMNuC5tbQoXKCB1u4JEeVp5V52cW\n" +
                "W/XFhOc8SsWnmsTJ8VhCeA7TsIdLTCxhJpWVO0ztkpwYeKSHe7nC2tK9lnyrPsnZ6+g+/PO1pXsq\n" +
                "x+da1ZLDP+91dBt2bdysElbK1cXZqJyB6o/DtJmHgVHx7uj23UetSmsevjQkHJ9VbqLILRAIfx0S\n" +
                "WvRo69qp1o5aC62HUGRCLiUlOr/aksnhYPCscLkyGU837KFcGq3Nbh0MnkVCLalVp5frbe29I98w\n" +
                "71+7KowAS8WnLQlKbV3WCzJp5bWkgmO9GmqL5HLH1i5MBo38XjafhLXmlolwzpmu2FEzO3342lW5\n" +
                "Tt+SoHTk8pBL5o0Z30zMzyzW+a0yqazCwVGX5FRTL6MJmO9k/fq//cdX/50TtvX7Mp0MarzZ84ZF\n" +
                "5kRPJpVtSU/1DvwsHku8eP7i8D998/SvtbeDvcsmdyI+uZte+f0fHqxsfvP0r+JXj90amQsbtsJA\n" +
                "DZPVmMnnCr/58DNhW0/PhVseiFbK1XzuTxaLJZ8tvBpN1Spzn3L0GOGIjbAEi6V1l6go1/89eL4u\n" +
                "t/PVjb5MKruXLRxZbsf4lX/NJ6FcB1JZokvle6FWtLeePmGxWPp9Up/k9PjcrbpiVq7WYAuL5B55\n" +
                "BLQxQtMTpjg0YyYJ5apKuiTnJ7c/aL572+s7ynchHEmrrpiVq1fdwgwH5XPJbWygySS8Hr4lzIRo\n" +
                "uFZsw91l6+6Ohkn6tdtdRi4PNXMUkmvMFhbJzecKi3N3NLkL2UQGmmlhJh5LCJe/AqMDTQtE47HE\n" +
                "4q/vxDcTcssAjfHi+YuvUrn/fBB//vxFn/utkz9uRrmDPrdT2J6VcrVVk6g3e94YDJ49cfKEyhPx\n" +
                "5jLQTBJGF1YPd32b3frLj640Ye0rnyv8y+9Wtu8+0m9f+0DFN3ve6NM/3/+UoyeTyh4edvZyhcHg\n" +
                "QKuWE2u1VQeDA5VytbFYw3QGmkbCTCr7cE1QwPcX4z9vwmt7Yzn2r79b0SRMqkfFZDydSWXfHvx7\n" +
                "vYdEYZHc1g6G375bu2z+Qe/I5aHTrp9aLJa/7H8j3MNoDwNNMydcnIseTpFpwmpepVz9fO5OS+oF\n" +
                "2ezW8ExI77muXMV4o90lls8VKuVKdH5V4VVoUgMtpsiYKRX3hUlqwzqvZORzhetTt1pVsas5pXuF\n" +
                "G4OVZ9Vd7c7XajODlZxyB5TMbqA5JJSriTKoZ6PXSmZouGLe8ExY18ROuYrx23eNVYdGIS/U7Aaa\n" +
                "Q0LhMBgYHdBvJd0gBn7nwyNdS6SNiM74lIpPjVM0vr0NNIGEyV3xbbXDum1LGMrAGvHNxMZyTKcP\n" +
                "9w96hacrto1RlK3tDTSDhKJh0CU5dUoTLRX3jWZgjQcrMf3mhwHR9SnJ3TQGIqFsV5A7J652LaRc\n" +
                "/XwuakADD+aHOoWIwtl15Vm1tR4qGGizW8O/CrVNMfXXDW6gUIm39dnF2lh+aPCKsYtzd/TIFuh1\n" +
                "9AjP9WlY/kxbA6fnwu10cNTQEspdJanHkkwmld2++8jgT6tUfLqx/FCfiHSgzvY3goFtVjzB4BIK\n" +
                "9ugU7jxRE4ga5LKUI9GpNNPbMjUIm7xGWilXFc7It6WBhpawVNwXBod6xKJb6zvNyUrThLWl+02L\n" +
                "SDNPsk02UC47ol0NNLSE+WxB1Fe6NY9FK+XqtiHvSJFjL1fQY6VUGGI0LWGoZqDcnLyNDTS0hMKg\n" +
                "S49YdGt9x7AronJsrGg/MxReMV2r1oGBnToSih5Jn/SWWYZBl+Ts90maX0B7MFvTfDAUJouXik/1\n" +
                "vtOzww20GPm6bGEgJHxbq0FlfYrD0bI/4A0Ez/6g05SK+5knud3NxxpGd7ubjzXfKHNJzsMy5LMF\n" +
                "/TIEMdC4EsrdL6d5b9jSKFPZZreOT12Us6LX0RMI9gSCA5lUVuESm+O+pErFfW0bxOOTBBLmCjpt\n" +
                "ymGgocPR0teCEEjz++XkFmAbWNK4ufRxPeOSx+eOzIbffe8dTb7/f2md0SJUWqcEBgw0uoQyE0KN\n" +
                "H4kmnTgwOhC+FjrWycaRy0PTs1PqL45VyKtsDGFZjWflCgZ2ooQV0YPXPBZVn5bVcI1Aj8+tvrjg\n" +
                "Xq6gbRabcN1L810KDDTzSKh1OKqye6m8BdE/6L1wRW3Fa20305tQ0gIDTSOhTBex6e35sRifUju1\n" +
                "G5sMCs/yNfOv+AHCPRWtfgsGmklC4Ril7ePZyxZUdlZNqjCNXTmvaiRsSo51RYtpIQa2w0ioLX9W\n" +
                "twc9otHNr4HggJoVGs1PNtn1qTiKgUgoQE0iiM1u1XDrTM1Hab5/IEzjLn39FAOR0KJ5/peakVDb\n" +
                "HctmXqTR/LbCQEZCXdBWm97T3e3aUBhoVgn1zhgGDETCo0YGA1xnCxhIOAoYiIQgj7a7cyrXHjEQ\n" +
                "CZuE5hmMp1QEvSo3+nVVWg/qbysMZCRs0sxT28K4aj7KpXWHFvpT5/otBiJh80ZCi3angeOxhJqj\n" +
                "/ZqnXDd8cAkD201CXdOIvx1D1G24f5XKaRJGqizZ1JyN/iNT55UN7HV0Y2CbjIQVTU+Xqu8Ta0v3\n" +
                "VaZubizHVNY71bxnN5A6r2ygS3J+cnsaA80nofCZ5bPNOLZzrOmTmhs8k7vpBytqLzzznNFyJBS+\n" +
                "U5Tzy480MDIbNtTN20ioKv7RPJNGfRXT+Gaisfr5mVRWfeF9l+TUtn8Lq4wqxO0Y2HkjodYnBjSp\n" +
                "qB/fTCzORY8Vl26t7yzMLKkvtaj5FXHCWENuBSufK3z64QIGtq2EvadFZb+0DkflLmA4dmAZT1+f\n" +
                "ulVPNd5MKjs/s/jlF/eN8xI5MtYQ7uXU7jOWm9Bi4LEwaN1R4UhYeVbVvNLmyKWh6IIG9zFVnlWj\n" +
                "C6sbKw+bVvxXjyviMvUVXFa+URwD20TCWic73GUzT3KBoJY9zz/otS1ZtSrCXSo+3b77qHbP4cGE\n" +
                "Tac7VQZFd1yrZK+OqwcwsFPCUbnBUPMML1uXdfjykB7ffy9X+CqV08nAXke35jXwhW3b6+h+1SgM\n" +
                "7CwJhdvQeqRZjlweUl+Ht8moLA8llvBJTvkpYGDnjYSilXE9LgnSbzDUCZfk1HwYtMiUQj6QEAM7\n" +
                "UUK5pUvNL2CoDYYq6382E/X1TkVvN/G1HLVVGQzsUAktFotHlNGi+QUMtcFQfVH65jB86Zwe+aLC\n" +
                "V5tLcvY6ejCwwyV0Cxc89ChC4/G5hy+dM/jT6nV0j02e1+OTha82j0/CwE6X0D/oFS6Z6BGRWiyW\n" +
                "scnzLmPnGYevXdWjx8vFon3SWxjY6RJaZArj6hGR1oLS96+FDLtSGpqe0Okswq5Mrs/a0j0MREJx\n" +
                "jvVerpDX5+bKXkfP9FzYgB5euBLUY0X0u5faY+HPMRAJvx0JheuW2+s7Ov3GPslpNA8DowNjk0Gd\n" +
                "Pjy5mz7WmUYM7DgJ5QbD+GZCvxrBhvJw+NI5XVduj1WnAwM7VMIRmZ303VhCv19qEA9D0xPjUxf1\n" +
                "+/xMKlt/Yh0Gdq6EvY4e4WC4vb6j+cVgP/Dw5tLHml9EUyc2u3V6dkq/eWCNjeUYBiJhfSHZ5XPC\n" +
                "ZYMt3WaG35rQZY3MhtVfan1c+n3SzaWP9S7iVP8wiIFIaPH43MIdvO31nSbcHjM2GfzH2x80Z0i0\n" +
                "2a2h6Ynm9Pg6h0EM1Jsf3bhxwxRf9OTJk4czjF88f1EpV/1aHzA/zJs9bwwGz55y9ORzf9IpBrbZ\n" +
                "rb8Y//kvP5r827/7mya0ZyaVfbCyiYFG4LWXL1+a5bteD98SLqZPz041857NeCyxdXdHwytybXbr\n" +
                "8OWhkctDzezrco2JgUh4xMt7YWZJ2Fc+uf1Bk79MPlfYXt9J7qbVnMr3B7z+gFfv1RdhIHpktUUM\n" +
                "REIx8zOLwrWEC1eC+m1nH2ljcjedSWX3soV6hOz3SX2S0+Nze864W9LFS8X96+HfYiASNt7jf/Ph\n" +
                "Z8KIzgiF1ivlaq10Zz5beHXq2Cc5bT+xnnL0GOH+U7kXGQYiYb2sLd2rVVIyQlBqOo4MRDGw+Zjv\n" +
                "VqaxyfPCRBaVRek7gXyugIFIqAEKp+C37z5K6nPUsA2olKuLc3cwEAm1wT/olbtGIjq/qtMpJ7Pz\n" +
                "+dwdhT2Jfp+EgUh4PEKRCWFQWnlWjc6v6ppTakai86sKizGB0QEMRMJGgtLwTEj4T3u5wvzMIo/2\n" +
                "gHgsoVCLIDA6YJYiV0hoODw+t1xq9V6uoP7isbYxUOGyDQxEQrWMTQbl8qobvjkQAwEJj8f7167K\n" +
                "lUjrcA8xEAmbNzmUW6TpZA8x0ESYL2NGiFw620GfG5+62DmrfxiIhEbseZ2zEy2X1oeBSNh6D3sd\n" +
                "3eFrV/uMXWNbDZVyNTq/KrxcCQOR0Cge2uzW8amLzT+/15yAPDq/qnDUGAOR0CgetuUUMR5LKJSs\n" +
                "x0AkbAHJ3XR0flWhU/Y6ukORiWYWxWhVCIqBSNjK8EzhRqEaw5fOjU2eN++QeOQAaLFY3n3vnRFT\n" +
                "3UOMhG1Fqbj/+VxUuSJTr6N77Mp5080SS8X9O4o52e09AUZCk0Vra0v3jrxKrd8njU0GTRGd1vkX\n" +
                "tf1SMBKajK31nS+/uH/kfzO4ipVydWt9Z3t958hyUv6ANxSZ4GgSEhpuiqi8gm9kFUvF/a31nXgs\n" +
                "caR+Nrv1wmSQSSASGjeQ21h+qJBNcniu6B/0tnY8Se6m45sJ5cXPV18fVyMTRijoBkioRCaVXVu6\n" +
                "X3/97MDogD/gbUKl/R+M2/HY42S83us7GQCR0HxsLMfqmVy92ss9Z9z+gNdzRtJvqKkVEa7fvYPX\n" +
                "xNhkkAEQCU0ZndazzCiMVD0+t8fndrmdKpcfS8X9fLaQzxWOdVmnkaevgISNaLCxHGtAxQNcktPW\n" +
                "Za2ZUKu0rRQMP8lZLJa9XOFZudKAda++CMbfu9jkIBmQ0NAqNo1+nzRyaQj9kLBtVdyNJY41V2wm\n" +
                "gdGBQHCA4BMJO4JjbQzojUtyBkYHBoNn2XxHwo6jUq4md9PJeLolNrokp8cnBYJnST1DQrBUytXM\n" +
                "k2wyns6kssfaPDgutY2Qfp/09qCXLQckBCUha5sKpeK+Sidtdmttn6NPekv9hgcgYYeSSWUt3+09\n" +
                "VMoVhetoDi4Jre1k9ElvMc0DJARoPa/TBABICICEAICEAEgIAEgIgIQAgIQASAgASAiAhACAhABI\n" +
                "CABICICEAICEAEgIAEgIgIQAgIQASAgASAiAhACAhABICABICICEAICEAEgIAEgIgIQAgIQASAgA\n" +
                "SAiAhACAhABICABICICEAICEAEgIAEgIgIQAgIQASAgASAiAhACAhABICABICICEAICEAEgIAEgI\n" +
                "gIQAgIQASAgASAiAhACAhABICABICICEAICEAEgIgIQAgIQASAgASAiAhACAhABICABICICEAICE\n" +
                "AEgIAEgIgIQAgIQASAgASAiAhACgK/8Pd+3UYgVOjPwAAAAASUVORK5CYII=\n" +
                "\"}",ArticleModel.class));
        articlesDefault.add(gson.fromJson("{\"page\":4,\"link\":\"https://developer.ibm.com/code/patterns/explore-hyperledger-fabric-through-a-complete-set-of-apis/\",\"title\":\"secure\",\"subtitle\":\"stack\",\"image\":\"Secure\",\"subtext\":\"\",\"description\":\"this app trades your data anonymously and securely for ‘kubecoins’\",\"imageEncoded\":\"iVBORw0KGgoAAAANSUhEUgAAASwAAAEsCAIAAAD2HxkiAAAACXBIWXMAAAsSAAALEgHS3X78AAAS\n" +
                "P0lEQVR42u2dPUwjaZqAa04b2GYDcEs+uXFSFk6QYSV3YNQkgDtpGggGApDcUcNmBxftrehgtQGc\n" +
                "dqOBzQYmaiQIYAOaNkkbSDzCwVgasJwYuRLT1loa08EZO+MCc9yMv8Ium7JdVX6esJoG11v1+H2/\n" +
                "/2/u7u4kAOgc/0YIAJAQAAkBAAkBkBAAkBAACQEACQGQEACQEAAJAQAJAZAQAJAQAAkBAAkBkBAA\n" +
                "kBAACQEACQGQEACQEAAJAQAJAZAQAJAQAAkBAAkBkBAAkBAACQEACQGQEACQEAAJAQAJAZAQAJAQ\n" +
                "AAkBAAkBkBAAkBAACQEACQGQEACQEAAJAQAJAZAQAJAQAAkBAAkBkBAAkBAACQEACQGQEACQEAAJ\n" +
                "AQAJAZAQAJAQAAkBAAkBOs7vCIHxySq560zul/xNOpmRJOkqqdT44QG/LEmSz+995urr97o9spsA\n" +
                "Gpxv7u7uiIIBKeRvfj5PXSWV9GWmdFtu+vfYHTbfkHfAL/9hZNDp6iOwSAia3IufJK6VnO6/vF92\n" +
                "BycC2IiEoE46mYlHE/GTRBv+VnAiEAwFfH4vYUdCuNcvshut3dJrBQN+eXIhhIpIiH4d0A8VkRCk\n" +
                "UrF8sH3UnuJTY4E6uzhl77HxaJCwK7g4T+1s7D+lz7MV2B228Mrc8MggDwgJLZ4Adzb2L+Ipw37C\n" +
                "4eBgeGWOlIiE1iSr5LbWPxTyXw3+OZ2u3qXVt4zyI6HViEcTB9tHRitBa5Sms4tTwVCAB4eE1jFw\n" +
                "Z3NfLz36vW5Hj71fyFTXSu62WLrO5PRSPbw8h4dIaAUOto/OPv74xPpwODjo83s9XreWyS6F/E02\n" +
                "k0snMxfx1BOr37Hpl7OLUzxEJDQxOxv7TY9DOF29wYkXI6HAU2aZFfI359FE/OSnpm0MTgTCK3M8\n" +
                "SiQ0JaeHsX/+8KmJ/zjgl8enR/UdLbg4T51+jDU3MYC6FAm7qB3YL7tnF9+0bv5KOpk52P7UxOxw\n" +
                "PERCk5FVcn/7z3809F/a2SF5ehg73o022n/zX9/9B+MWSGgOSsXyXxb/3tAr3v4h8iamDdgdtr9u\n" +
                "/4lxfCQ0ARvvtxpqen377s34zKgpWq0DfnllbYlHrC/sMaMzDa2KsDtsy2uLnTJQkqTxmdHltUW7\n" +
                "Q2tyu0oqkd0oTxkJDd0UPN6LNmDg+lLH1xD5/N7l9SXtHh7vRbMtWPWPhKAPB9tHDRlokH4Oj+xu\n" +
                "yEPttwlI2Fbi0YTGQtRQBjbh4VVSiUcTPHEkNBalYjmy99mkBjbhYWTvc6lY5rkjoYE4PYxpnBc2\n" +
                "uzhl2NE2j+zWOFO0kP96ehjjuSOhgdLgmbY3srLNmZHvJRgKBCc0fcKzwxjJEAmNwsV5SsvQvNPV\n" +
                "a4oVCbOLU05Xb/2vntvyxXmKp4+EhkBja9As20bYe2wal01ovHFAwpanQS2twcqCQLPclM/vHQ7W\n" +
                "X8NRyH8lGSKhASTUNv1ydvGNue5L4wc28qZVSNgVlIplLWt2gxMB0x3/4HT1aemhiZ8k6J5Bwg7X\n" +
                "olp+bHIhZMa70/ixqUiRsJNUDgys2xo06SlITleflpahliAAEnYyE2ocdjMmWj48mRAJO0ZWqb+5\n" +
                "oN1hM/XG8sMjg3UnspVuy6yrQMIO1aKXGS0vsdlvU8staAkFIKH+aNkuSUubyugSariFazIhEnaE\n" +
                "X/I3dX/GN2T6c/+03IKWUAAS6k/d1YP9stsC2yLZe2z99ZZ9dPacUyTsUrSMUD8z58hEczfCkD0S\n" +
                "tpus8qXuz/RbZZdOLTeiJSCAhO3GMlvlsucvEhqRwr/qr5yw/94i++RquREtAQEk1BP6AwkIEgJY\n" +
                "hN8RglY2pZ7r8ntKxXJW+VL6n/vZYZUTeVV/8uEEX4/stv/e5pGf6zJGYpluXiTsOpoWoJC/SV8q\n" +
                "WeVLVsk1evx11SrbyvHaHtntkZ/7huTm1nM4kRAJu4FSsZy+zFzEU+lk5olnXP/m196Wr5LKw2C6\n" +
                "09Vb2brCN+TlfCUkhHv3Ls5TF/FUe/aJKOS/xk8Sld0AhoODw8HB4ZFBbETCLiWdzMSjiaZPtH86\n" +
                "9+Zv3u+GaqJ9qJAQnko8mojsfdax5nzq5zlJxE8STlfv5PwrzsRGQotXnqeHsbPDWKPnVEuSNOCX\n" +
                "n7n6nK4+e4/N4601fyWbyZWK5UL+5pf8TUPzqgv5rzub+wfbR2Mzo+Mzo9SoSGg1Gj0mfsAv+/xe\n" +
                "j+z2eN0NdU5WVZWF/E02k8squXQyo8XJ0m35eC96dhh7vRDq4OmlSAi6Nr3OUwc/HGkpPit7Yejb\n" +
                "del09TldfcMjg5IUeuiArbtvf+m2/M8fPp19jM2+m7LA5gBI2L0U8jcfNvbr5p8H91r9utt7bMMj\n" +
                "g8Mjg9KKdN8lW9PGQv7r1n/vDPjlt9p2xYfm+Obu7o4oNEFkN1r3ZGy7w1Y74VT6Qjo4SFAZIKnb\n" +
                "S1T3RiRJej0fMun2qmRCK1PjxR3wy5MLoY6PCth7bMFQIBgKpJOZyG70saTdRE8SIKFxMYh+Vfj8\n" +
                "3pU1b20VAQlNj9PVG16ZM/KY+IOKOxv7xhnGRELQo+pz2MZmRs3SZPL5vX/d+lNkN9rckCYgoRHr\n" +
                "z7crc6ZbiDC5EBoJBbT07sITYVFva/n23ZuVtSXzHgizsrb07bs3PEcyoVlbgEurby2wRdL4zKhv\n" +
                "yLu1/oFWIpnQTAwHB//83bKVdlv783fLFtjSHwm7hbHpl0urYYtNgLb32JZWw2PTL3m+SGgIskru\n" +
                "7DCm+k/h5bnZxSmr3vjs4lR4WX0K29lhjAPSkLBNlIrlzdUt1b778PKc5dfjBUMBVQ9Lt+XN1a0C\n" +
                "Gx8iYRsM3HjfvQbW9fD79R0OpUDC1rKzsa96Fl/3GFjbw2slt7Oxz3uChK0ishtV3Y6p2wys7eFF\n" +
                "PBXZjfK2IKH+pJMZ1bVL375707X7sgRDAdWh/OO9aDrJAdpIqHdTcGttR+UtnAh0+R4Q4zOjwQmV\n" +
                "76CtNRqHSKh3U1DsjOmX3WGWnEtSeGVOPMCwdFumcYiEulHZCaLqot1hW1lbIjgVVtaW7A6b2Di8\n" +
                "OE8RHCTUoRBV/UZfeh9mU8D//0rqsS29D6tXEBSlSPhEIrufxUJ0bPol+1VX4fN7xUltpdtyZPcz\n" +
                "wUHC5skqubOPP1ZddLp6JxdeERyRyYVXTldv1cWzjz8ynQ0Jm+dg+0i1H4JC9LGiVLWnSjWMgIT1\n" +
                "Ud2yOjjBwSl1ilJxxOIqqTBsiITNtQarh+btDpuFV0joxezilNhTyhwaJNQnDY5xTIq2onRMmMBA\n" +
                "MkRCHdKg09XLDtMamVwIiT00JEMkbICskhPT4OQ8PaKNeCiEi2SIhA0grpp3uno5PbMhgqGAmAzj\n" +
                "0QSRQcL6lIpl8QjrsWlO6msYMWjxkwQTaJCwPufRn6p7Ghy2kdALItMoI6EXYjepGF5AQkn8tq66\n" +
                "0sGjy9pxvy0rESvHIdYNLyDhbyjkb8TdK8asu2JwZ2N/Z3O/dWuOxNBdKzk2g0LCWvwsLL3pl92W\n" +
                "2cNXNLCSl+IniRZ56JHd4lLDn1nfhIQN1aKqy8atZODDjbfIQzGAVKRI2Fgt+oeRQcsb2FIPxQBS\n" +
                "kSLho6QvFbEWNemBSo0a2DoPna4+sSIVQ42EIEmSJM7n8Pnl7jGwdR6KYWTqDBJqlXDYWrVoXQNb\n" +
                "5KG4+AsJkVCFUrEsnr9npaWDGg188FDH+dbid1kh/5WpM0hYTVb5IjYIu9PAyr3ru5+qGEwx4EjY\n" +
                "9bWo0FVgmQZhEwaurC3pO0lIpVlI3wwSViEOTnjk5xioF2Iwr9n9CQmruC2Wqq44/70XA/VCDKYY\n" +
                "cCTsdsRVvGbvlTGOgarBFAOOhGApDGUgIGF9xGGrATP3yhjTwAGG7JGQHEgOREIz0bblS/quqTWy\n" +
                "gc8sNwsXCfUkm6nuLrf32NuTtXRcU2vwHChOhRfDjoTdS0emUOm7ptaMVSgz15DQQC23J3pIOxAJ\n" +
                "QQdnmvYQA5EQdHOmCQ8xEAlB56zVkIcYiITQkrpRo4cYiISWwjfU8vkxkd1oQ2tqa3toDQPbEHYk\n" +
                "NDG6z6ganxltaJVwDQ9NaiCT1JCww9h7bCtrS0/3kCoUCa2JuOS0FRtjPt1DUxsohtQay6aRUDc9\n" +
                "hDfmq9HyodlzoBhS8jMSVr+yVVeyrdl/oTkP/7L0d1MbKAaz36InfCChrsnwXzet+1uNethQZjZg\n" +
                "O1AMJmkQCasR91/ItnInoiY8NK+BqsG00p6uSKgP4mq3Vnept8JDw/aFisFkeSESCq+vt1qG69av\n" +
                "dtPXQyOPRojBFAOOhN2OR3ZXHbBeui1nFdN4aGQDs0qudPubpYN2h81DxwwSqjQLh4RzSy7bMcnj\n" +
                "6R4afEReDKMYaiQESVLbDuwi3qaDnZ/iofHnxIhhHPAzaxQJtWXCq6TSti0YmvPQ+AaWimWVjZXJ\n" +
                "hEj4WLPQ6arerf3iPNW2D9Coh6aYFyoG0OnqpUGIhI8nQ2Hwqm0VaaMemmVmthhARgiRsBbDwUHx\n" +
                "HWrzpmBaPDSLgaViWZRQDDISwq/ej5HBqoEKSZLOoz+1+WPU9tBEq5PE0NkdNosdQo6E+hMMBaqu\n" +
                "nH2Mtf9jPOahudYHiqETwwtIKEr4oupKIf+1nd0zNTw0l4EX5ylx3rkYXkDCajyyW8w/p51IhlUe\n" +
                "mm6NvBi0ftlNvygSamJ8erTqylVS6dQWKRUPgxMBcxmYTmbE4UExsICEjzYLxQHDyG60U5/H3mML\n" +
                "r8yZawGeGC6nq5cGIRI24uHEC+MkQ9OhmgbFkAIS1qxIZ0bFsYqD7U9ERgtioOwO2/gMtSgSNlgB\n" +
                "jgkvzbWSOz2MEZzanB7GroUlYGMzo+xngYTNJEOxZXi8G+VUvRqUiuVjtdYgaRAJm0yGk/Ovql+y\n" +
                "27JeB+takp2N/ar1u5IkTc6/Ig0iYZMEQwHVRYYdGbs3PhfnKdWlg3SKIuGTmF2cUv++pygVClHV\n" +
                "GkE1gICEDeCR3WPTL8Wi9Pv1DwTn13y//kEsRMemXzJFBgl1YHLhldhDc5VUOjh8bzQiu1FxYNDp\n" +
                "6p1ceEVwkFAHKhNWxOvHe9F4NEF84tHE8Z7K95HpZvkgoaHx+b2v50Pi9YPtozZsi2hkskruYPtI\n" +
                "vP56PsQKeiTUvSgNiT2lpdvy5upW13qYVXKbq1tiU3DAL08uhHhnkFB//rj6VpzLVhk57MLO0kp3\n" +
                "qGig3WH74+pb3hYkbFXjcHl9SfTwWsltvN/qKg9LxfLG+y1xeprdYVte52BgJGwlHtmtOvDVVR4+\n" +
                "ZqAkSbOLU4xJNMo3d3d3RKFR4tHEzqbKwHQlD1j7LXysHShJUnh5jskxZMI2EQwFxBF8qQv6aWoY\n" +
                "GJwIYCCZsN3UOETekjnhsfxfMVB1KBWQsJMeWuy97J47RULzEdmNqs4XkSSpX3aHV+bM3kTMKrmd\n" +
                "jf3rR2rs1/MhhgSR0NB1mt1he70QMu+S1tPD2PFuVLURKNETg4SG4uI8pTpyXWHAL5uu774yH02c\n" +
                "lv3w5RJemWNDeyQ0U9lWqdzGzbDVSqlYPj2MPVZjW6bMRkJrUpnJVeM0NeNXp7XrT0mShoODLI9A\n" +
                "QnO3o6TKQrv5V0ZrTcWjicjeZ/H0CMu0b5Gwuyjkb75f37muOWpvd9jGZkY7XqBWis+zw1iNbw1K\n" +
                "UCS0bEqsEJwIDAcH29/JUdmX6bHRPxIgElonJR5sf9Jy5nbl9Mw22Fhx7+I8VffbodICnF1843T1\n" +
                "8SiR0NykkxnVXVges9E35B3wy74hr17lX1bJpS8zV0klfZnR4p70fwtzWR2PhN2r4q9leObqc7r6\n" +
                "fEOyJElarKgcXJO+VAr5m1/yN038RfRDQlTUpErVFV1+J/ohYRe1FSO7UY0Ns1ZTaY5OLoRo+yFh\n" +
                "11Eqlu+7SeKd2Vd/OHjfFcTgOxJi472N2jtOnpL3fENe3ENCeJSHnsys8qXG/JWGcLp6PfJzfXtc\n" +
                "AQm7JUNmlS/ZTK5ULFf6PK8zudqp0u6w9XvdkiT5/F57j83jdXvk52Q8JASAR2GjJwAkBEBCAEBC\n" +
                "ACQEACQEQEIAQEIAJAQAJARAQgBAQgAkBAAkBEBCAEBCACQEACQEQEIAQEIAJAQAJARAQgBAQgAk\n" +
                "BAAkBEBCAEBCACQEACQEQEIAQEIAJAQAJARAQgBAQgAkBAAkBEBCAEBCACQEACQEQEIAQEIAJAQA\n" +
                "JARAQgBAQgAkBAAkBEBCAEBCACQEACQEQEIAQEIAJAQAJARAQgBAQgAkBAAkBEBCAEBCgM7zv75j\n" +
                "KV/Aa+dmAAAAAElFTkSuQmCC\n" +
                "\"}",ArticleModel.class));
        articlesDefault.add(gson.fromJson("{\"page\":5,\"link\":\"https://developer.ibm.com/code/patterns/cloud-based-air-traffic-control-application/?S_TACT=C34409NW\",\"title\":\"modernize\",\"subtitle\":\"\",\"image\":\"telescope\",\"subtext\":\"\",\"description\":\"augmented assets are served from IBM’s Object Store we’re just getting started\",\"imageEncoded\":\"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\"}",ArticleModel.class));
        articlesDefault.add(gson.fromJson("{\"page\":6,\"link\":\"https://github.com/IBM/indoor-map-iphone-app\",\"title\":\"manage\",\"subtitle\":\"microservices\",\"image\":\"Microservices\",\"subtext\":\"built to share\",\"description\":\"the dashboard, the phone data, the blockchain are all managed independently\",\"imageEncoded\":\"iVBORw0KGgoAAAANSUhEUgAAASwAAAEsCAIAAAD2HxkiAAAACXBIWXMAAAsSAAALEgHS3X78AAAD\n" +
                "UElEQVR42u3dL07DUADA4ZZMADcgqJLK3gIFBgsHmCTzoMcBwOJbi2lVbzG3ZAeo6HaDh0DTYNbx\n" +
                "2u+zXba91/7ymuzPS0MICXA6Z6YARAgiBEQIIgRECCIERAgiBEQIIgRECCIERAgiBEQIIgRECCIE\n" +
                "RAgiBEQIIgRECCIERAgiBEQIIgRECCIERAgiBEQIIgRECCIERAgiBEQIIgRECCIERAgiBEQIIgRE\n" +
                "CCIERAgiBEQIIgRECCIERAgiBEQIIgRECCIERAgiBEQIJ7eYwBieH15Ge62Pr7ejPn9dtk3VjjOW\n" +
                "vMhW66UzYiUEt6OACEGEgAhBhIAIQYSACEGEgAhBhIAIQYSACEGEgAhBhIAIQYSACEGEgAhBhIAI\n" +
                "QYSACEGEgAhBhIAIQYSACEGEgAghZmkIYSZDHd5IPa6tz4e3tj/eZvTOiJUQRAiIEEQIiBBECIgQ\n" +
                "RAiIEEQIiBBECIgQRAiIEEQIiBBECIgQRAiIEEQIiBBECIgQRAiIEEQIiBBECIgQRAiIEEQIiBBE\n" +
                "CIgQRAiIEEQIiBBECIgQRAjztjAFP+qyjejdbje7gaN9t49rOCIkSZKkqaZz1fbdYUrDcTsKiBBE\n" +
                "CIgQRAiIEEQIiBAmZ0Yf1udFNpmx9N2+7w6/Hb24PL++uXJxxyINIZiF6NRlO/CdmLzIVuulWXI7\n" +
                "CogQRAiIEEQIiBBECIgQRAiIEEQIiBBECIgQRAiIEEQIiBBECIgQRAgiBEQIIgRECCIERAgiBEQI\n" +
                "IgRECCIERAgiBEQIIgRECCIERAgiBEQIE5WGEMzC/1GXbVO147xWXmSr9dKcWwlBhIAIQYSACEGE\n" +
                "gAhBhIAIYW4WpiAid4+390+3f3/8drN7f/00b1ZCQIQgQkCEIEJAhCBCQIQgQkCEIEJAhCBCQIQg\n" +
                "QsDvCWPSVOP9PzdWQhAhIEIQISBCECEgQhAhIEKYKnvWg5UQRAiIEEQIiBBECIgQRAiIEEQIiBBE\n" +
                "CIgQRAiIEEQIiBBECIgQRAiIEEQIiBBECIgQRAiIEEQIiBBECIgQRAiIEEQIiBBECIgQRAiIEEQI\n" +
                "iBBECIgQRAiIEEQIiBBECIgQRAiIEEQIiBBECIgQRAiIEEQIiBBECIgQRAiIEEQIs/cN2nNWgs8z\n" +
                "ECUAAAAASUVORK5CYII=\n" +
                "\"}",ArticleModel.class));

        adapter = new ArticlePagerAdapter(rootView.getContext(), articles);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
        requestPages();

        return rootView;
    }

    public static TechFragment newInstance() {
        return new TechFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void requestPages() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, BACKEND_URL + "/pages", null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        articles.clear();
                        ArticleModel[] articleModels = gson.fromJson(response.toString(), ArticleModel[].class);
                        articles.addAll(Arrays.asList(articleModels));
                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "That didn't work!", error);
                articles.clear();
                articles.addAll(articlesDefault);
                adapter.notifyDataSetChanged();
            }
        });
        this.queue.add(jsonArrayRequest);
    }

}
