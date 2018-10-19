cd java
javac -cp lib/*:. *.java
cd ..


java -cp java/lib/'*':java/ ConnectedComponents -s wikipedia_pt
java -cp java/lib/'*':java/ it.unimi.dsi.webgraph.Stats -s wikipedia_pt

java -cp java/lib/'*':java/ it.unimi.dsi.webgraph.Transform transpose wikipedia_pt wikipedia_pt_trans
java -cp java/lib/'*':java/ it.unimi.dsi.webgraph.algo.BetweennessCentrality wikipedia_pt wikipedia_pt_betweenness.out

#for i in {1..7}
#do
#	java -server -Xss256K -Xms35G -XX:PretenureSizeThreshold=512M -XX:MaxNewSize=4G -XX:+UseNUMA -XX:+UseTLAB -XX:+UseConcMarkSweepGC -XX:CMSInitiatingOccupancyFraction=00 -XX:+UseCMSInitiatingOccupancyOnly -verbose:gc -Xloggc:gc.log -cp java/lib/'*':java/ it.unimi.dsi.webgraph.algo.HyperBall -l 12 -n wikipedia_pt_neighbourhood_function_$i.out -d wikipedia_pt_sum_of_distances.out -h wikipedia_pt_harmonic_centrality.out -c wikipedia_pt_closeness_centrality.out wikipedia_pt_trans wikipedia_pt
#done

java -cp java/lib/'*':java/ ApproximateNeighbourhoodFunctionStats wikipedia_pt_neighbourhood_function_1.out wikipedia_pt_neighbourhood_function_2.out wikipedia_pt_neighbourhood_function_3.out wikipedia_pt_neighbourhood_function_4.out wikipedia_pt_neighbourhood_function_5.out wikipedia_pt_neighbourhood_function_6.out wikipedia_pt_neighbourhood_function_7.out > wikipedia_pt_anf_stats.out

python python/graphs.py
