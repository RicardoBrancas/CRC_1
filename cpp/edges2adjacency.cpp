#include <iostream>
#include <fstream>
#include <map>
#include <set>

int main(int argc, char** args) {

	std::map<int, std::set<long>> v;

	std::ifstream s(args[1]);

	long a, b;
	while (s >> a >> b) {
		a = a-1;
		b = b-1;
		if (v.find(a) == v.end())
			v[a] = std::set<long>();

		v[a].insert(b);
	}

	for (long i = 0; i < v.size(); i++) {
		for (auto it = v[i].begin(); it != v[i].end(); it++) {
			std::cout << *it;
			if (std::next(it) != v[i].end())
				std::cout << " ";

		}
		std::cout << std::endl;
	}



	return 0;
}
