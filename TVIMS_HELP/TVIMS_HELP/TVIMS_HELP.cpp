#include <iostream>
#include <fstream>
#include <map>
#include <math.h>
using namespace std;

int main()
{
    ifstream input("data.txt");
    size_t n;
    input >> n;
    int sum = 0;
    map<int, int> nums_count;
    for (int i = 0; i < n; i++) 
    {
        int num;
        input >> num;
        sum += num;
        nums_count[num]++;
    }
    cout << 1 + floor(3.32 * log10(n)) << endl;
    cout << "sum: " << sum << endl;
    double m = (double)sum / n;
    sum = 0;
    for (auto num : nums_count) 
        sum += (num.first - m) * (num.first - m) * num.second;
    double d = (double)sum / n;
    cout << "d: " << d << endl;
    for (auto num : nums_count) 
        cout << num.first << ": " << num.second << endl;
    return 0;
}