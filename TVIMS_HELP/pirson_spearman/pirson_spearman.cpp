#include <fstream>
#include <vector>
#include <string>
#include <algorithm>
#include <stack>
#include <map>
#include <iostream>

using namespace std;

float** pirson(const vector<vector<float>>& values)
{
    int n = values[0].size();
    float** result = new float*[5];
    for (int i = 0; i < 5; ++i)
        result[i] = new float[5];
    float average[5];
    for (int i = 0; i < 5; ++i) 
    {
        double sum = 0.0f;
        for (int j = 0; j < n; ++j) 
            sum += values[i][j];
        average[i] = (float)(sum / n);
    }
    for (int i = 0; i < 5; ++i) 
    {
        for (int j = i; j < 5; ++j) 
        {
            double cov = 0.0f;
            double divx = 0.0f;
            double divy = 0.0f;
            for (int e = 0; e < n; ++e) 
            {
                double xi = values[i][e] - average[i];
                double yj = values[j][e] - average[j];
                cov += xi * yj;
                divx += xi * xi;
                divy += yj * yj;
            }
            double div = sqrt(divx * divy);
            float resij = (float)(cov / div);
            result[i][j] = resij;
            result[j][i] = resij;
        }
    }
    return result;
}

float find_rank(int start, int end) 
{
    start++;
    end++;
    return ((float)(start + end) * (end - start + 1)) / 2;
}

float** spearman(const vector<vector<float>>& values)
{
    int n = values[0].size();
    float** result = new float*[5];
    for (int i = 0; i < 5; ++i)
        result[i] = new float[5];
    vector<map<float, float>> ranks(5);
    for (int i = 0; i < 5; ++i) 
    {
        float* temp = new float[n];
        for (int j = 0; j < n; ++j)
            temp[j] = values[i][j];
        sort(temp, temp + n);
        int start = 0;
        for (int j = 1; j < n; ++j) 
        {
            if (temp[j - 1] != temp[j]) 
            {
                float r = find_rank(start, j - 1) / (j - start);
                ranks[i][temp[start]] = r;
                start = j;
            }
        }
        ranks[i][temp[start]] = find_rank(start, n - 1) / (n - start);
        delete[] temp;
    }
    for (int i = 0; i < 5; ++i) 
    {
        for (int j = i; j < 5; ++j) 
        {
            uint64_t sum = 0;
            for (int e = 0; e < n; ++e) 
            {
                if (ranks[i][values[i][e]] <= 0)
                    cout << "wtf";
                if (ranks[j][values[j][e]] <= 0)
                    cout << "wtf";
                int d = ranks[i][values[i][e]] - ranks[j][values[j][e]];
                sum += d * d;
            }
            int znam = n * (n * n - 1);
            float p = 1 - 6 * sum / (float)znam;
            result[i][j] = p;
            result[j][i] = p;
        }
    }
    return result;
}

int main()
{
    ifstream in("data.csv");

    int count = 0;
    vector<vector<float>> values(5);
    string line;
    bool first_line = true;
    while (getline(in, line))
    {
        if (first_line) 
        {
            first_line = false;
            continue;
        }
        bool end = false;
        int k;
        for (k = 0; k < line.length(); ++k) 
        {
            if (line[k] == ',') 
            {
                if (!end) 
                {
                    end = true;
                }
                else 
                {
                    k++;
                    break;
                }
            }
        }
        int j = 0;
        string num = "";
        for (int i = k; i < line.length(); ++i)
        {
            if (line[i] != ',') 
            {
                num.push_back(line[i]);
            }
            else 
            {
                float val = (float)atof(num.c_str());
                values[j++].push_back(val);
                num.clear();
            }
        }
        float val = (float)atof(num.c_str());
        values[j++].push_back(val);
        num.clear();
    }

    ofstream out("pirson.txt");
    float** result = pirson(values);
    for (int i = 0; i < 5; ++i) 
    {
        for (int j = 0; j < 5; ++j) 
        {
            out << result[i][j] << " ";
        }
        out << endl;
    }
    out.close();
    for (int i = 0; i < 5; ++i)
        delete[] result[i];
    delete[] result;

    ofstream out2("spearman.txt");
    float** result2 = spearman(values);
    for (int i = 0; i < 5; ++i) 
    {
        for (int j = 0; j < 5; ++j) 
        {
            out2 << result2[i][j] << " ";
        }
        out2 << endl;
    }
    for (int i = 0; i < 5; ++i)
        delete[] result2[i];
    delete[] result2;

    return 0;
}