import * as React from 'react';
import Box from '@mui/material/Box';
import Rating from '@mui/material/Rating';
import Typography from '@mui/material/Typography';

export default function BasicRating({first, last}:{first:string, last:string}) {
  const [value, setValue] = React.useState<number | null>(2);

  return (
    <Box className="rating"
      sx={{
        '& > legend': {
            marginLeft: '10px', // Adjust the value as needed
            marginRight: '10px', // Adjust the value as needed
        },
      }}
    >
      <Typography component="legend">{first} </Typography>
      <Rating
        name="simple-controlled"
        value={value}
        onChange={(event, newValue) => {
          setValue(newValue);
        }}
      />
      <Typography component="legend"> {last} </Typography>
    </Box>
  );
}