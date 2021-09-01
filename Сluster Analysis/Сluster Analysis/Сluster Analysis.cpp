#include <fstream>
#include <string>
#include <vector>
#include <algorithm>
#include <iostream>
#include <ctime>

using namespace std;

vector<vector<pair<int, int>>>* cluster_analysis(const vector<int>& x_coords, const vector<int>& y_coords, int k = 3)
{
    srand(time(0));
    vector<vector<pair<int, int>>>* result1 = new vector<vector<pair<int, int>>>(k);
    vector<vector<pair<int, int>>>* result2 = new vector<vector<pair<int, int>>>(k);
    int n = x_coords.size();
    int x_max = *max_element(x_coords.begin(), x_coords.end());
    int y_max = *max_element(y_coords.begin(), y_coords.end());
    float* x_rand = new float[k];
    float* y_rand = new float[k];
    for (int i = 0; i < k; ++i) 
    {
        x_rand[i] = rand() % x_max;
        y_rand[i] = rand() % y_max;
        cout << "x_rand[" << i << "] = " << x_rand[i] << endl;
        cout << "y_rand[" << i << "] = " << y_rand[i] << endl;
    }
    
    // 1 проход
    for (int i = 0; i < n; ++i) 
    {
        int x = x_coords[i];
        int y = y_coords[i];

        float min_dist = (x_rand[0] - x) * (x_rand[0] - x) 
            + (y_rand[0] - y) * (y_rand[0] - y);
        int min_index = 0;
        for (int j = 1; j < k; ++j) 
        {
            float dist = (x_rand[j] - x) * (x_rand[j] - x)
                + (y_rand[j] - y) * (y_rand[j] - y);
            if (dist < min_dist) 
            {
                min_dist = dist;
                min_index = j;
            }
        }
        result1->at(min_index).push_back(pair<int, int>(x, y));
    }
    // остальные проходы
    int c = 0;
    while (++c <= 500)
    {
        bool smth_changed = false;
        for (int i = 0; i < k; ++i) 
        {
            float x_mid = 0.0f;
            float y_mid = 0.0f;
            for (pair<int, int> vec : result1->at(i)) 
            {
                x_mid += vec.first;
                y_mid += vec.second;
            }
            x_mid /= result1->at(i).size();
            y_mid /= result1->at(i).size();
            x_rand[i] = x_mid;
            y_rand[i] = y_mid;
        }

        for (int i = 0; i < k; ++i) 
        {
            for (pair<int, int> vec : result1->at(i)) 
            {
                int x = vec.first;
                int y = vec.second;
                float current_dist = (x_rand[i] - x) * (x_rand[i] - x)
                    + (y_rand[i] - y) * (y_rand[i] - y);
                int index = i;
                for (int j = 0; j < k; ++j) 
                {
                    if (j == i)
                        continue;
                    float min_dist = (x_rand[j] - x) * (x_rand[j] - x)
                        + (y_rand[j] - y) * (y_rand[j] - y);
                    if (min_dist < current_dist) 
                    {
                        smth_changed = true;
                        current_dist = min_dist;
                        index = j;
                    }
                }
                result2->at(index).push_back(pair<int, int>(x, y));
            }
            result1->at(i).clear();
        }

        swap(result1, result2);
        if (!smth_changed) 
        {
            cout << c << endl;
            break;
        }
    }

    delete[] x_rand;
    delete[] y_rand;
    delete result2;
    return result1;
}

int main()
{
    ifstream in("data.csv");

    int count = 0;
    vector<int> x_coords;
    vector<int> y_coords;
    string line;
    while (getline(in, line)) 
    {
        bool a = false;
        string num = "";
        for (int i = 0; i < line.length(); ++i) 
        {
            if (line[i] != ';') 
            {
                num.push_back(line[i]);
            }
            else
            {
                x_coords.push_back(stoi(num));
                num.clear();
            }
        }
        y_coords.push_back(stoi(num));
    }

    ofstream out1("2_clusters.txt");
    vector<vector<pair<int, int>>>* result1 = cluster_analysis(x_coords, y_coords, 2);
    out1 << '{';
    for (int i = 0; i < 2; ++i) 
    {
        out1 << '{';
        for (pair<int, int> vec : result1->at(i)) 
        {
            out1 << '{' << vec.first << ", " << vec.second << "}, ";
        }
        out1 << "}, ";
    }
    out1 << '}';
    out1.close();
    delete result1;

    ofstream out2("3_clusters.txt");
    vector<vector<pair<int, int>>>* result2 = cluster_analysis(x_coords, y_coords, 3);
    out2 << '{';
    for (int i = 0; i < 3; ++i)
    {
        out2 << '{';
        for (pair<int, int> vec : result2->at(i))
        {
            out2 << '{' << vec.first << ", " << vec.second << "}, ";
        }
        out2 << "}, ";
    }
    out2 << '}';
    out2.close();
    delete result2;

    ofstream out3("4_clusters.txt");
    vector<vector<pair<int, int>>>* result3 = cluster_analysis(x_coords, y_coords, 4);
    out3 << '{';
    for (int i = 0; i < 4; ++i)
    {
        out3 << '{';
        for (pair<int, int> vec : result3->at(i))
        {
            out3 << '{' << vec.first << ", " << vec.second << "}, ";
        }
        out3 << "}, ";
    }
    out3 << '}';
    out3.close();
    delete result3;

    in.close();
    return 0;
}