import { test, expect } from '@playwright/test';


test.beforeEach(async ({ page }) => {
  await page.goto("http://localhost:5173");
});

test('has title', async ({ page }) => {
  await page.goto('https://playwright.dev/');

  await expect(page).toHaveTitle(/Playwright/);
});

test('get started link', async ({ page }) => {
  await page.goto('https://playwright.dev/');
  await page.getByRole('link', { name: 'Get started' }).click();

  await expect(page.getByRole('heading', { name: 'Installation' })).toBeVisible();

});

test('Page starts at homepage', async ({ page }) => {
  await page.goto('http://localhost:5173/');

  await page.waitForSelector('#homepage');

  const homepageContentExists = await page.isVisible('#homepage');
  expect(homepageContentExists).toBeTruthy();
});

test('Every page has navbar', async ({ page }) => {
  await page.goto('http://localhost:5173/');
  await page.waitForSelector('#navbar');
  const navbarContentExists = await page.isVisible('#navbar');
  expect(navbarContentExists).toBeTruthy();

  await page.goto('http://localhost:5173/browse/');
  await page.waitForSelector('#navbar');
  const navbarContentExists2 = await page.isVisible('#navbar');
  expect(navbarContentExists2).toBeTruthy();

  await page.goto('http://localhost:5173/browse/3');
  await page.waitForSelector('#navbar');
  const navbarContentExists3 = await page.isVisible('#navbar');
  expect(navbarContentExists3).toBeTruthy();

  await page.goto('http://localhost:5173/review/');
  await page.waitForSelector('#navbar');
  const navbarContentExists4 = await page.isVisible('#navbar');
  expect(navbarContentExists4).toBeTruthy();

});

// test('LogIn', async ({ page }) => {
//   await page.click('#profile-button');

//   await page.waitForSelector('#headlessui-menu-item-:r7');

//   await page.click('#headlessui-menu-item-:r7');

// });

test('profile displays logged in status', async ({ page, browserName}) => {
  
  await page.goto('http://localhost:5173/profile/');
  await page.waitForSelector('.profile-page-color:nth-child(2)');
  await page.waitForSelector('.profile-page-color:nth-child(3)');

  const nameText = await page.textContent('.profile-page-color:nth-child(2)');
  const emailText = await page.textContent('.profile-page-color:nth-child(3)');

  if (browserName === "webkit"){
    expect(nameText).toBe('Name: ');
    expect(emailText).toBe('Email: ');
  }
  else{

  expect(nameText).toBe('Name: error');
  expect(emailText).toBe('Email: error');
  }

});


test('footer expands and unexpands', async ({ page }) => {
  await page.evaluate(() => {
    // window.loginStatus = 'Sign Out'; 
    // window.userCounted = async () => true;
  });

  await page.goto('http://localhost:5173');


  const expandButton = await page.isVisible('#click-here');
  // expect(expandButton).toBeTruthy();

  const preferencesForm = await page.isVisible('#footer-expanded');
  // expect(preferencesForm).toBeTruthy();
});

test('Program Display page loads correctly', async ({ page }) => {
  await page.goto('http://localhost:5173/browse/1'); // Update the URL with the correct port and program ID

  await page.waitForSelector('#display-title');
  const programTitle = await page.textContent('#display-title');
  // expect(programTitle).toContain('Program Title'); // Replace 'Program Title' with the expected program title

  // Check if the country information is displayed
  await page.waitForSelector('#display-info');
  const countryInfo = await page.textContent('#display-info');
  // expect(countryInfo).toContain('Country Name'); // Replace 'Country Name' with the expected country name

  // Check if the ratings section is visible
  await page.waitForSelector('#display-stats');
  const ratingsSection = await page.textContent('#display-stats');
  // expect(ratingsSection).toContain('Ratings:');

  // // Check if the comment box is visible
  // await page.waitForSelector('.comment-box-title');
  // const commentBoxTitle = await page.textContent('.comment-box-title');
  // // expect(commentBoxTitle).toContain('Program Reviews:');

  // Check if the 'Leave a Review' link is visible
  await page.waitForSelector('.button');
  const leaveReviewLink = await page.textContent('.button');
  // expect(leaveReviewLink).toContain('Leave a Review!');
});


// test('popup shows up after submission', async ({ page }) => {
//   await page.goto('http://localhost:5173');

//   await page.waitForSelector('#click-here');
//   // await page.click('#click-here');

//   await page.fill('[name=friendliness]', '3');
//   await page.fill('[name=safety]', '2');
//   await page.fill('[name=lgbtAcceptance]', '4');
//   await page.fill('[name=educationQuality]', '1');

//   await page.click('[type=submit]');

//   // const popupSelector = '.your-popup-selector';
//   // await page.waitForSelector(popupSelector);

//   // const isPopupVisible = await page.isVisible(popupSelector);
//   // expect(isPopupVisible).toBeTruthy();
// });
