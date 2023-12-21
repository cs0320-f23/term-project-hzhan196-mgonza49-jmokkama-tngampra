from back.src.main.scraping.LoadPrograms import LoadProgram
import pymongo

def test_test():
    assert sum([1, 2, 3]) == 6, "Should be 6"

def test_webscrape_different_site():
    load_program = LoadProgram()
    


if __name__ == "__main__":
    test_test()
    print("Everything passed")