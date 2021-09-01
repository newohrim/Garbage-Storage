#include <fstream>
#include <map>
using namespace std;

int main()
{
    setlocale(LC_ALL, "RUS");
    ifstream input("input.txt");
    ofstream output("output.txt");

    int n;
    input >> n;
    map<double, int> counts;
    for (int i = 0; i < n; ++i) 
    {
        double num;
        input >> num;
        counts[num]++;
    }
    for (auto element : counts)
        output << element.first << " " << element.second << endl;
    input.close();
    output.close();
    return 0;
}