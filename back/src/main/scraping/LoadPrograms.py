from selenium import webdriver
from selenium.webdriver.chrome.service import Service as ChromeService
from selenium.webdriver.common.by import By
import os
import pymongo
# import com.mongodb.ConnectionString;


class LoadProgram:
    def __init__(self, site):
        # self.chrome_path = '/path/to/chromedriver'  # Replace with the path to your chromedriver executable
        chrome_service = ChromeService()
        self.driver = webdriver.Chrome(service=chrome_service)

        # STUDY_ABROAD_WEBSITE = "https://studyabroad.brown.edu/explore/explore-programs"
        self.driver.get(site)
        self.driver.implicitly_wait(10)

        # Find the div with class 'dsf-items' using Selenium
        self.t_body = self.driver.find_element(By.CLASS_NAME, 'dsf-items')
        self.programs = {}
    
    def webScrape(self):
        if self.t_body:
            # Find all divs with class "sc-1mdqp61-0 euqYpv views-row dsf-item" inside the outer div
            inner_divs = self.t_body.find_elements(By.CLASS_NAME, 'dsf-item')

            # Loop through the inner divs and do something
            for inner_div in inner_divs:
                try:
                    program_name = inner_div.find_element(By.CLASS_NAME, 'dsf-link')
                    location = inner_div.find_element(By.CLASS_NAME, "term-item")

                    if (program_name and location):
                        span_text = program_name.find_element(By.TAG_NAME, 'span').text.strip()
                        link_href = program_name.find_element(By.TAG_NAME, 'a')

                        if link_href:
                            link_href = link_href.get_attribute('href')

                        # print("Span Text:", span_text)
                        # print("Link Href:", link_href)

                        text_content = location.text.strip()
                        # print(text_content)

                        self.programs[span_text] = [link_href, text_content]

                    else:
                        print("There was an error")
                except: 
                    print("No <a> element found within program_name")

        else:
            print("Outer div with class 'dsf-items' not found.")
        # Close the Selenium WebDriver
        self.driver.quit()
        
        return self.programs

    def insertDatabase(self, program_data):
        # ConnectionString mongoUri = new ConnectionString("mongodb+srv://tngampra:cs0320-admin@cs0320.chqlqki.mongodb.net/?retryWrites=true&w=majority");
        client = pymongo.MongoClient("mongodb+srv://tngampra:cs0320-admin@cs0320.chqlqki.mongodb.net/?retryWrites=true&w=majority")
        dbname = "study-abroad"
        collectionName = "program-data"
        mydb = client[dbname]
        mycol = mydb[collectionName]

        for program_name in program_data.keys():
            query = { "name" : program_name }
            existing_names = mycol.find(query)
            if len(list(existing_names)):
                continue
            else:
                value_lst = program_data[program_name]
                insert_dict = { 
                    "name" : program_name,
                    "link" : value_lst[0],
                    "location" : value_lst[1],
                    "comments" : [] }
                mycol.insert_one(insert_dict)
                # print(program_name + value_lst[1])
                # print(value_lst[0])

        # myquery = { "name" : "test" }
        # mydoc = mycol.find(myquery)

        # print(len(list(mydoc)))

        # for x in mydoc:
        #     print(x)

        # myquery = { "name" : "fish" }
        # mydoc2 = mycol.find(myquery)


if __name__ == "__main__":
    STUDY_ABROAD_WEBSITE = "https://studyabroad.brown.edu/explore/explore-programs"
    loader = LoadProgram(STUDY_ABROAD_WEBSITE)
    programs = loader.webScrape()
    # print(programs)
    loader.insertDatabase(programs)


    
