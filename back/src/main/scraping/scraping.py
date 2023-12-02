from bs4 import BeautifulSoup
from selenium import webdriver
from selenium.webdriver.chrome.service import Service as ChromeService
from selenium.webdriver.common.by import By
import os

# Set up Selenium WebDriver
chrome_path = '/path/to/chromedriver'  # Replace with the path to your chromedriver executable
chrome_service = ChromeService()
driver = webdriver.Chrome(service=chrome_service)

STUDY_ABROAD_WEBSITE = "https://studyabroad.brown.edu/explore/explore-programs"

# Use Selenium to get the dynamic content
driver.get(STUDY_ABROAD_WEBSITE)

# Wait for the page to load (you might need to adjust the time based on your network speed)
driver.implicitly_wait(10)

# Get the page source after JavaScript execution
html_content = driver.page_source

# Use BeautifulSoup to parse the HTML content
soup = BeautifulSoup(html_content, 'html.parser')

# Rest of your code remains the same

file_path = 'output.html'
prettified_html = soup.prettify()

# Write the HTML content to the file
if not os.path.exists(file_path):
    # Create the file if it doesn't exist
    with open(file_path, 'w', encoding='utf-8') as file:
        file.write(prettified_html)
    print(f'File created: {file_path}')
else:
    print(f'File already exists: {file_path}')

# Find the div with class 'dsf-items' using Selenium
t_body = driver.find_element(By.CLASS_NAME, 'dsf-items')

programs = {}

# Save data below
if t_body:
    # Find all divs with class "sc-1mdqp61-0 euqYpv views-row dsf-item" inside the outer div
    inner_divs = t_body.find_elements(By.CLASS_NAME, 'dsf-item')

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

                print("Span Text:", span_text)
                print("Link Href:", link_href)

                text_content = location.text.strip()
                print(text_content)

                programs[span_text] = [link_href, text_content]

            else:
                print("There was an error")
        except: 
            print("No <a> element found within program_name")

else:
    print("Outer div with class 'dsf-items' not found.")

print(programs)

# Close the Selenium WebDriver
driver.quit()

# import sys

# print(sys.executable)
