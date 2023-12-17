import * as React from "react";
import Box from "@mui/material/Box";
import Rating from "@mui/material/Rating";
import Typography from "@mui/material/Typography";
import { useField } from "formik";

export default function BasicRating({
  first,
  last,
  name,
}: {
  first: string;
  last: string;
  name: string;
}) {
  // const [value, setValue] = React.useState<number | null>(2);
  const [field, , helpers] = useField<number | null>({ name });

  return (
    <Box
      className="rating"
      sx={{
        "& > legend": {
          marginLeft: "10px", // Adjust the value as needed
          marginRight: "10px", // Adjust the value as needed
        },
      }}
    >
      <Typography component="legend">{first} </Typography>
      <Rating
        name="simple-controlled"
        value={field.value || 0}
        onChange={(event, newValue) => {
          helpers.setValue(newValue);
        }}
      />
      <Typography component="legend"> {last} </Typography>
    </Box>
  );
}
