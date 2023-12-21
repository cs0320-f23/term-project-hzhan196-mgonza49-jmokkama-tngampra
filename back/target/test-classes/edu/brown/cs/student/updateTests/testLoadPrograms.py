import sys
# for p in sys.path:
#     print( p )
personal_path =  "/Users/earth/Documents/CS 0320/term-project-hzhan196-mgonza49-jmokkama-tngampra"
sys.path.insert(1, personal_path + "/back/src/main/scraping" )
from LoadPrograms import LoadProgram
import pymongo
import selenium
import unittest

class TestLoadProgram(unittest.TestCase):
    def test_test(self):
        self.assertTrue(sum([1, 2, 3]) == 6), "Should be 6"

    def test_webscrape_database(self):
        load_program = LoadProgram("https://studyabroad.brown.edu/explore/explore-programs")
        programs = load_program.webScrape()
        self.assertTrue("American Council: Advanced Russian Language & Area Studies Program (RLASP)" in programs.keys())
        # print(programs.values())
        self.assertTrue(["https://brown.via-trm.com/client/programs/10609", "ISRAEL"] in programs.values())
        self.programs = programs

        client = pymongo.MongoClient("mongodb+srv://tngampra:cs0320-admin@cs0320.chqlqki.mongodb.net/?retryWrites=true&w=majority")
        dbname = "study-abroad"
        collectionName = "program-data"
        mydb = client[dbname]
        mycol = mydb[collectionName]

        find_program = mycol.find_one({"name" : "CASA: Brown Global Program / CASA Havana"})
        find_fish = mycol.find_one({"name" : "ig: @smelly_fisherman"})
        self.assertTrue(find_program != None)
        self.assertTrue(find_fish == None)

        mycol.delete_one({"name" : "CASA: Brown Global Program / CASA Havana"})
        find_program2 = mycol.find_one({"name" : "CASA: Brown Global Program / CASA Havana"})
        self.assertTrue(find_program2 == None)

        load_program.insertDatabase(programs)
        find_program3 = mycol.find_one({"name" : "CASA: Brown Global Program / CASA Havana"})
        self.assertTrue(find_program3 != None)

if __name__ == "__main__":
    unittest.main()
    print("Everything passed")