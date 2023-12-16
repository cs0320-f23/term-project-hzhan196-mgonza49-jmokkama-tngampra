import * as React from 'react';
import Radio from '@mui/material/Radio';
import RadioGroup from '@mui/material/RadioGroup';
import FormControlLabel from '@mui/material/FormControlLabel';
import FormControl from '@mui/material/FormControl';
import Typography from '@mui/material/Typography';

export default function RowRadioButtonsGroup({first, last}:{first:string, last:string}) {
  return (
    <FormControl style={{display: 'flex', flexDirection: 'row', justifyContent: 'center', alignItems: 'center'}}>
      {/* <FormLabel id="demo-row-radio-buttons-group-label">How Accepting Communities Are</FormLabel> */}
      <Typography component="legend">{first} </Typography>
      <RadioGroup
        row
        aria-labelledby="demo-row-radio-buttons-group-label"
        name="row-radio-buttons-group"
        style={{marginLeft: '3vh'}}
      >
        <FormControlLabel value="1" control={<Radio style={{ color: 'white' }} />} label="" />
        <FormControlLabel value="2" control={<Radio style={{ color: 'white' }} />} label="" />
        <FormControlLabel value="3" control={<Radio style={{ color: 'white' }}/>} label="" />
        <FormControlLabel value="4" control={<Radio style={{ color: 'white' }}/>} label="" />
    
      </RadioGroup>
      <Typography component="legend">{last} </Typography>
    </FormControl>
  );
}